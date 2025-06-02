package com.eltonkola.engine

import Category
import Nav
import Page
import TemplateContext
import TemplateEngine
import com.eltonkola.model.BllokConfig
import com.eltonkola.model.BlogConfig
import com.eltonkola.model.parseBlogPost
import java.io.File

fun pageRenderer(
    options: BllokConfig,
    config: BlogConfig,
    pageFiles: List<File>,
    recentsFiles : List<File>,
    templatePage: String,
    fileName: String,
    post: File? = null
) {

    val posts = pageFiles.map { parseBlogPost(it.path) }
    val recents = recentsFiles.map { parseBlogPost(it.path) }

    val pages = listOf(
        Page("Home"),
        Page("About"),
        Page("Music"),
        Page("Contact")
    )

    val navs = listOf(
        Nav("News"),
        Nav("Flash")
    )

    val categories = listOf(
        Category("Technology", listOf(
            Category("Web Development"),
            Category("Software Engineering", listOf(
                Category("Backend"),
                Category("Frontend")
            ))
        )),
        Category("Design", listOf(
            Category("UI/UX"),
            Category("Graphic Design")
        )),
        Category("Programming")
    )

    val context = TemplateContext(
        mapOfNotNull(
            "websiteName" to config.websiteName,
            "websiteCopyright" to config.copyright,
            "pages" to pages,
            "navs" to navs,
            "categories" to categories,
            "posts" to posts,
            "recentposts" to recents,
            "post" to post
        )
    )

    val template = File(options.templatePath,  templatePage).readText().trimIndent()

    val engine = TemplateEngine("${options.templatePath}/partials")

    val renderedHtml = engine.render(template, context)

    File(options.outputPath, fileName).writeText(renderedHtml)

}

fun List<File>.recents(size: Int = 3) : List<File>{
    if(this.size < size) return this
    return this.subList(0, size - 1)
}

fun <K, V> mapOfNotNull(vararg pairs: Pair<K, V?>): Map<K, V> {
    return pairs.mapNotNull { (k, v) -> v?.let { k to it } }.toMap()
}
