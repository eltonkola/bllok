package com.eltonkola.bllok

import com.eltonkola.bllok.data.DataManager
import com.eltonkola.bllok.generator.BllokGenerator

class Bllok(
    templatePath : String,
    outputPath : String
) {

    companion object{
        lateinit var templatePath : String
        lateinit var outputPath : String

        const val version = "1.0"
    }
    init{
        Bllok.templatePath = templatePath
        Bllok.outputPath = outputPath
    }

    private val dataManager = DataManager()

    fun execute(){
        val appData = dataManager.loadAppData()
        val generator = BllokGenerator(appData)
        generator.generate()
    }

}
