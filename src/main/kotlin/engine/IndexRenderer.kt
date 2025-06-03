package com.eltonkola.engine

import Nav
import Page
import PagingItem
import TemplateContext
import TemplateEngine
import com.eltonkola.model.BllokConfig
import com.eltonkola.model.BlogConfig
import com.eltonkola.model.BlogFile
import com.eltonkola.model.BlogPost
import com.eltonkola.model.Category
import com.eltonkola.model.parseBlogPost
import java.io.File
import kotlin.collections.List

fun pageRenderer(
    categories: List<Category>,
    pages: List<Page>,
    paging : List<PagingItem>,
    options: BllokConfig,
    config: BlogConfig,
    pageFiles: List<BlogFile>,
    recentsFiles : List<BlogFile>,
    templatePage: String,
    fileName: String,
    post: BlogPost? = null,
    navs: List<Nav> = emptyList()
) {

    val posts = pageFiles.map { it.parseBlogPost() }
    val recents = recentsFiles.map { it.parseBlogPost() }

    val context = TemplateContext(
        mapOfNotNull(
            "websiteName" to config.websiteName,
            "websiteCopyright" to config.websiteCopyright,
            "websiteDescription" to config.websiteDescription,
            "baseUrl" to config.baseUrl,

            "feedEmail" to config.feedEmail,
            "feedEmailRealName" to config.feedEmailRealName,
            "socials" to config.socials,
            "language" to config.language,

            "pages" to pages,
            "navs" to navs,
            "categories" to categories,
            "posts" to posts,
            "recentposts" to recents,
            "post" to post,
            "paging" to paging
        )
    )

    val template = File(options.templatePath,  templatePage).readText().trimIndent()

    val engine = TemplateEngine(options)

    val renderedHtml = engine.render(template, context)

    File(options.outputPath, fileName).writeText(renderedHtml)

}

fun List<BlogFile>.recents(size: Int = 3) : List<BlogFile>{
    if(this.size < size) return this
    return this.subList(0, size - 1)
}

fun <K, V> mapOfNotNull(vararg pairs: Pair<K, V?>): Map<K, V> {
    return pairs.mapNotNull { (k, v) -> v?.let { k to it } }.toMap()
}
