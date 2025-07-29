package dev.gbenga.dsagithub.nav

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.toList
import kotlinx.coroutines.launch
import java.io.Serializable

interface Screen: Serializable{

}

fun main() {
    val channel = Channel<Int>(Channel.BUFFERED)
    repeat(100) {
        channel.trySend(it)
    }
    channel.close()
// The check can fail if the default buffer capacity is changed
    CoroutineScope(Dispatchers.IO).launch {
        check(channel.toList() == (0..<64).toList())
    }


}