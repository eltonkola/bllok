package com.eltonkola.bllok

import com.eltonkola.bllok.data.DataManager
import com.eltonkola.bllok.generator.BllokGenerator

class Bllok {

    private val dataManager = DataManager()
    private val generator = BllokGenerator()

    fun execute(){
        val appData = dataManager.loadAppData()
        generator.generate(appData)
    }

}