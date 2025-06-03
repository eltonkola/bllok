package com.eltonkola

import com.eltonkola.model.BllokConfig
import com.eltonkola.model.buildCategoryTree
import com.eltonkola.model.parseYamlConfig
import java.io.File
import java.time.Instant
import kotlin.system.exitProcess


fun main(args: Array<String>) {

    val startedAt = Instant.now()
    println("Bllok start!")
    if(args.size == 3) {
        val templatePath = args[0]
        val inputPath = args[1]
        val outputPath = args[2]
        println("templatePath: $templatePath")
        println("inputPath: $inputPath")
        println("outputPath: $outputPath")

        if(templatePath.isEmpty() || inputPath.isEmpty() || outputPath.isEmpty()) {
            println("!!! please pass the template and output path sa parameters !!!")
            exitProcess(1)
        }
        Bllok(BllokConfig(
            templatePath = templatePath,
            inputPath = inputPath,
            outputPath = outputPath,
            debug = true //TODO - remove
        )).execute()
    }else{
        println("!!! please pass the template and output path sa parameters !!!")
        exitProcess(1)
    }
    println("Bllok end!")
    println("Executed in: ${Instant.now().toEpochMilli() - startedAt.toEpochMilli() } millisec!")
    exitProcess(0)
}
class Bllok(
    private val options: BllokConfig
){
    fun execute() {
        println("templatePath: ${options.templatePath}")
        println("inputPath: ${options.inputPath}")
        println("outputPath: ${options.outputPath}")

        //blog settings
        val config = parseYamlConfig("${options.inputPath}/bllok.yaml")
        //tree of blog pages to create
        val contentTree = buildCategoryTree(directory = File(options.inputPath))

        if (options.debug) {
            println("Blog title: ${config.websiteName}")
            contentTree.printTree()
        }

        //create root publish folder
        File(options.outputPath).clearFolder()
        copyStaticFiles(File(options.templatePath), File(options.outputPath))

        BllokGenerator(
            config = config,
            root = contentTree,
            isRoot = true,
            options = options
        ).generate()

    }

    fun File.clearFolder() {
        if (this.exists()) {
            this.listFiles()?.forEach { it.deleteRecursively() }
        } else {
            this.mkdirs()
        }
    }
    fun copyStaticFiles(from: File, to: File) {
        if (!from.exists() || !from.isDirectory) {
            error("Source folder '${from.path}' does not exist or is not a directory.")
        }
        if (!to.exists()) to.mkdirs()

        from.walkTopDown().forEach { file ->
            val relative = file.relativeTo(from)
            val target = File(to, relative.path)

            // Skip excluded files and folders
            if (
                file.name == "index.html" ||
                file.name == "post.html" ||
                file.name.startsWith("partial_")
            ) return@forEach

            if (file.isDirectory) {
                target.mkdirs()
            } else {
                file.copyTo(target, overwrite = true)
            }
        }
    }



}
