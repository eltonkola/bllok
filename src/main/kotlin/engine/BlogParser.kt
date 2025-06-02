package com.eltonkola.engine

import Category
import Nav
import Page
import TemplateContext
import TemplateEngine
import com.eltonkola.model.fakePosts
import java.io.File

fun main() {

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

    // Create template context
    val context = TemplateContext(
        mapOf(
            "websiteName" to "My Static Site",
            "websiteCopyright" to "© 2024 My Website",
            "pages" to pages,
            "navs" to navs,
            "categories" to categories,
            "posts" to fakePosts,
            "recentposts" to fakePosts.toMutableList().subList(0, 2),
            "post" to fakePosts.firstOrNull() // For single post pages
        ) as Map<String, Any>
    )

    val template = File("C:\\Users\\test\\Documents\\GitHub\\bllok\\skenderbeu_blog\\modern_template\\index.html").readText()
        .trimIndent()

    val engine = TemplateEngine("C:\\Users\\test\\Documents\\GitHub\\bllok\\skenderbeu_blog\\modern_template\\partials")

    val renderedHtml = engine.render(template, context)

    File("C:\\Users\\test\\Documents\\GitHub\\bllok\\skenderbeu_blog\\modern_template\\output\\index.html").writeText(renderedHtml)

}
