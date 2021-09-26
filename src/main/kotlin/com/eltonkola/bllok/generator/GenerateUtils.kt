package com.eltonkola.bllok.generator

import com.eltonkola.bllok.Bllok
import java.io.File

enum class Tag(val open: String, val close: String){
    INCLUDE("<include>","</include>"),
    ARTICLES("<articles>","</articles>"),
    CATEGORIES("<categories>","</categories>"),
    PAGING_PAGES("<paging>","</paging>"),
}

enum class ContentTags(val tag: String){
    TITLE("\$title"),
    DESCRIPTION("\$description"),
    TEXT("\$text"),
    HREF("\$href"),
    PUB_DATE("\$pub_date"),
    CATEGORY("\$category"),
}

fun saveFile(fileName: String, fileContent: String){
    File(fileName).bufferedWriter().use { out -> out.write(fileContent) }
}

fun cleanDirectory(fileName: String){
    var dir = File(fileName)
    dir.deleteRecursively()
    dir.mkdirs()
}

fun readFile(fileName: String)
        = File(fileName).inputStream().readBytes().toString(Charsets.UTF_8)

//this method will open a file, replace the include tags and return the full content
fun openFile(fileName: String) : String {
    val fileContent = readFile(fileName)
    var inflatedtext = fileContent
    fileContent.findTagList(Tag.INCLUDE){
        val includeFileName = it.cleanTag(Tag.INCLUDE)
        val includedText = readFile("${Bllok.templatePath}${includeFileName}")
        inflatedtext = inflatedtext.replace(it, includedText)
    }
    return inflatedtext
}

fun String.cleanTag(tag: Tag) : String {
    var text = this.replace(tag.open, "")
    text = text.replace(tag.close, "")
    return text
}

fun String.findTagList(tag: Tag, onMatch : (String) -> Unit){
    val regex = "${tag.open}(.*?)${tag.close}".toRegex(RegexOption.MULTILINE)
    val matchResult = regex.findAll(this)
    if(matchResult.toList().isNotEmpty()){
        matchResult.map {it.groups[1]!!.value}.forEach {
            onMatch(it)
        }
    }
}