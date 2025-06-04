package com.eltonkola.engine


object Log {
    var showLogs = false

    fun println(message: String) {
        if (showLogs) {
            kotlin.io.println(message)
        }
    }

    fun println(message: String, force: Boolean){
        if(force){
            kotlin.io.println(message)
        }else{
            println(message)
        }

    }
}