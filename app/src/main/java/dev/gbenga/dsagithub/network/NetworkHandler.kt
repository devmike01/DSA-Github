package dev.gbenga.dsagithub.network

import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import dev.gbenga.dsa.collections.list.LinkedList
import dev.gbenga.dsagithub.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import kotlin.coroutines.CoroutineContext

class NetworkHandler( val baseUrl: String,
                     val gson: Gson,
                     val ioContext: CoroutineContext = Dispatchers.IO) {

    companion object{
        const val TAG = "NetworkHandler"
    }

    var url: URL? = null

    suspend inline fun <reified T, reified R> get(path: String): Result<R>{
        //val get = Get::class.annotations.find { it.annotationClass == Get::class} as? Get
        url = URL("${baseUrl}/$path")
        return httpConnection<T, R>() ?: Result.failure(Exception("Unknown error has occurred"))
    }

    @Suppress("UNCHECKED_CAST")
    suspend inline fun <reified T, reified R> httpConnection(): Result<R>?{
        return withContext(ioContext){
            try {
                val urlConnection = (url?.openConnection() as HttpURLConnection?)?.apply{
                    setRequestProperty(TAG, BuildConfig.GH_SECRET)
                }
                val list = LinkedList<T>()
                var reader : JsonReader? = null
                urlConnection?.let {
                    try {
                        reader = JsonReader(InputStreamReader(urlConnection.inputStream))
                        val data = gson.fromJson<Any>(reader, TypeToken.get(Any::class.java).type)

                        if(data is ArrayList<*>){ //data
                            val values = data as ArrayList<T>
                            values.forEach { value ->
                                value.let {
                                    Log.d(TAG, "value_valA1 -> $value")

                                    val body = gson.toJson(value)
                                    val innerType =  gson.fromJson<T>(body, T::class.java)

                                    list.append(innerType)
                                }
                            }
                            Result.success(list as R)
                        }else{
                            Result.success(data as R)
                        }

                    }finally {
                        urlConnection.disconnect()
                    }

                }
            }catch (ex: Exception) {
                Log.e("NetworkHandler", "$ex")
                Result.failure(ex)
            }
        }
    }
}