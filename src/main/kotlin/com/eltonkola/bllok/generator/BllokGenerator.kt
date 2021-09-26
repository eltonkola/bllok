package com.eltonkola.bllok.generator

import com.eltonkola.bllok.Bllok
import com.eltonkola.bllok.data.model.AppData
import java.io.File

class BllokGenerator{

    fun generate(appData: AppData){

        //clean the output folder
        cleanDirectory(Bllok.outputPath)

        val index = openFile("${Bllok.templatePath}index.html")
        saveFile("${Bllok.outputPath}index.html", index)

        //copy res folder
        File("${Bllok.templatePath}res").copyRecursively(File("${Bllok.outputPath}res"), true)

    }

}
