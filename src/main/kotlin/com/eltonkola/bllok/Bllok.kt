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
    }
    init{
        Bllok.templatePath = templatePath
        Bllok.outputPath = outputPath
    }

    private val dataManager = DataManager()
    private val generator = BllokGenerator()

    fun execute(){
        val appData = dataManager.loadAppData()
        generator.generate(appData)
    }

}
