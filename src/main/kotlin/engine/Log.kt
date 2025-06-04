package com.eltonkola.engine


object Log {
    var showLogs = false

    fun println(message: String){
        kotlin.io.println(message)
    }

    fun println(message: String, force: Boolean){
        println(message)
    }
}