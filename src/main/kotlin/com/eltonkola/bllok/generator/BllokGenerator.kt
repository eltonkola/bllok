package com.eltonkola.bllok.generator

import com.eltonkola.bllok.data.model.AppData

class BllokGenerator(
    val templatePath : String,
    val outputPath : String
) {

    fun generate(appData: AppData){

        //clean the output folder

        cleanDirectory(outputPath)

        val index = openFile("${templatePath}index.html")
        saveFile("${outputPath}index.html", index)

    }

}
