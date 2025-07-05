package dev.gbenga.dsagithub.base

import com.google.gson.Gson
import dev.gbenga.dsagithub.BuildConfig
import dev.gbenga.dsagithub.network.NetworkHandler
import org.koin.dsl.module

val appModules = module {
    single { Gson() }
    single{NetworkHandler(BuildConfig.BASE_URL, get())}
}