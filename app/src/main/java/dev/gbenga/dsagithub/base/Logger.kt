package dev.gbenga.dsagithub.base

import dev.gbenga.dsagithub.BuildConfig

class Logger {

    object Holder{
        val instance = Logger()
    }

    fun d(tag: String, message: Any){
        if (BuildConfig.DEBUG){
            println("$tag: $message")
        }
    }
}