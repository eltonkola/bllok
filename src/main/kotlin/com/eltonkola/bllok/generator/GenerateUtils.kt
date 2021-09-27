package com.eltonkola.bllok.generator

import com.eltonkola.bllok.Bllok
import com.eltonkola.bllok.data.model.Article
import com.eltonkola.bllok.data.model.Label
import java.io.File
import java.time.LocalDate
import java.time.format.DateTimeFormatter

enum class Tag(val open: String, val close: String, val conditional: String? = null){
    INCLUDE("<include>","</include>"),
    ARTICLES("<articles>","</articles>"),
    CATEGORIES("<categories>","</categories>"),
    PAGING_PAGES("<paging>","</paging>"),
    IF_CURRENT("<ifCurrent>", "</ifCurrent>","<else>"),
    PREVIOUS_PAGE("<prev>","</prev>"),
    NEXT_PAGE("<next>","</next>"),
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
    val dir = File(fileName)
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
    tag.conditional?.let{
        text = text.replace(it, "")
    }
    return text
}

fun String.findTagList(tag: Tag, onMatch : (String) -> Unit){
    val regex = "${tag.open}(.*?)${tag.close}".toRegex(RegexOption.DOT_MATCHES_ALL)
    val matchResult = regex.findAll(this)
    if(matchResult.toList().isNotEmpty()){
        matchResult.map {it.groups[0]!!.value}.forEach {
            onMatch(it)
        }
    }else{
        println("no matches for $tag")
    }
}

fun getIndexPageName(page: Int) : String {
    return if(page == 0) "index.html" else "index_${page + 1}.html"
}

fun getIndexPageLink(page: Int) : String {
    return if(page == 0) "/index.html" else "/index_${page + 1}.html"
}

fun getCategoryPageName(page: Int, category:Label) : String {
    return if(page == 0) "tag_${category.name.cleanForUrl()}.html" else "tag_${category.name.cleanForUrl()}_${page+1}.html"
}

fun getCategoryPageLink(page: Int, category:Label) : String {
    return if(page == 0) "/tag_${category.name.cleanForUrl()}.html" else "/tag_${category.name.cleanForUrl()}_${page+1}.html"
}

fun getArticlePageName(article: Article) : String {
    return "read_${article.title.cleanForUrl()}.html"
}

fun getArticlePageLink(article: Article) : String {
    return "/read_${article.title.cleanForUrl()}.html"
}

fun String.cleanForUrl() : String {
    return this.replace(" ", "_")
}

fun String.getSummary() : String {
    return if(this.length > 50){
        this.substring(0, 50)
    }else{
        this
    }
}
fun LocalDate.toReadableDate() : String {
    var formatter = DateTimeFormatter.ofPattern("dd-MMMM-yyyy")//"yyyy-MM-dd HH:mm:ss"
    return formatter.format(this)
}

