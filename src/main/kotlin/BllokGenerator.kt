package com.eltonkola

import Nav
import Page
import PagingItem
import com.eltonkola.engine.Log
import com.eltonkola.engine.pageRenderer
import com.eltonkola.engine.recents
import com.eltonkola.engine.rss.renderRssFeed
import com.eltonkola.engine.sort
import com.eltonkola.model.BllokConfig
import com.eltonkola.model.BlogConfig
import com.eltonkola.model.BlogFile
import com.eltonkola.model.BlogPost
import com.eltonkola.model.Category
import com.eltonkola.model.parseBlogPost
import com.eltonkola.model.toBreadcrumbNavs
import java.io.File

class BllokGenerator(
    private val parentPages: List<Page> = emptyList(),
    private val parentCategories: List<Category> = emptyList(),
    private val config: BlogConfig,
    private val root: Category,
    private val isRoot: Boolean,
    val options: BllokConfig
){

    val blogPages : List<Page> get() {
        if(parentPages.isNotEmpty()){
            return parentPages
        }
        val allPages = root.subcategories.firstOrNull { it.name == "Pages" }
        return allPages?.getAllFiles()?.map { page ->
            val post = page.parseBlogPost()
            Page(post.metadata.title, post.link)
        } ?: emptyList<Page>()
    }

    val blogCategories : List<Category> get() {
        if(parentCategories.isNotEmpty()){
            return parentCategories
        }
        return root.subcategories.filter { it.name != "Pages" }
    }



    fun generate(){
        //we will generate a category, this may be the root folder, or a subfolder on a tree of content
        Log.println("config: $config")
        Log.println("inputPath: $root")

        val allFiles = root.getAllFiles().sort()
        val recents = allFiles.recents(5)
        //1. generate index page
        renderIndex(
            allFiles = allFiles,
            recents = recents
        )
        //2. generate every page
        val pages = root.getPosts()
        pages.forEach { renderPage(post = it, recents = recents) }
        //3. generate every subcategory)
        root.subcategories.forEach { renderSubcategory(it) }
        //4. only if root, crete a rss feed
        if(isRoot){
            renderRss(
                allFiles = allFiles
            )
        }

    }

    fun renderIndex(
        allFiles: List<BlogFile>,
        recents: List<BlogFile>
    ){
        //make directory if it does not exist
        val currentDir = File(options.outputPath, root.path)
        currentDir.mkdirs()
        //scan all docs in the tree, and generate a list of X last posts

        val pages = allFiles.chunked(config.postsPerPage)
        Log.println("pages: $pages")

        pages.forEachIndexed { index, page ->

            Log.println("index: $index - page: $page")

            val fileName = if(index == 0) "index.html" else "index_${index}.html"


            val paging = createPaging(pages.size, index, windowSize = 2)

            pageRenderer(
                categories = blogCategories,
                pages = blogPages,
                options = options,
                config = config,
                pageFiles = page,
                paging = paging,
                recentsFiles = recents,
                templatePage = "index.html",
                fileName = root.path + "/" + fileName,
            )
        }

        if(pages.isEmpty()){
            pageRenderer(
                categories = blogCategories,
                pages = blogPages,
                paging = emptyList(),
                options = options,
                config = config,
                pageFiles = emptyList(),
                recentsFiles = recents,
                templatePage = "index.html",
                fileName = root.path + "/index.html"
            )
        }

    }

    private fun createPaging(
        totalPages: Int,
        currentIndex: Int,
        windowSize: Int = 1 // Number of pages before and after current page
    ): List<PagingItem> {
        val items = mutableListOf<PagingItem>()

        // Add Prev button
        if (currentIndex > 0) {
            items += PagingItem("Prev", if (currentIndex - 1 == 0) "index.html" else "index_${currentIndex - 1}.html", false)
        }

        // Always show first page
        items += PagingItem("1", "index.html", currentIndex == 0)

        // Show ellipsis if needed
        if (currentIndex > windowSize + 1) {
            items += PagingItem("...", "#", false)
        }

        // Window around current page
        val start = maxOf(1, currentIndex - windowSize)
        val end = minOf(totalPages - 2, currentIndex + windowSize)
        for (i in start..end) {
            if (i != 0 && i != totalPages - 1) {
                items += PagingItem(
                    label = "${i + 1}",
                    link = "index_${i}.html",
                    selected = i == currentIndex
                )
            }
        }

        // Show ellipsis before last page if needed
        if (currentIndex < totalPages - windowSize - 2) {
            items += PagingItem("...", "#", false)
        }

        // Always show last page if more than one
        if (totalPages > 1) {
            val lastIndex = totalPages - 1
            items += PagingItem("${lastIndex + 1}", "index_${lastIndex}.html", currentIndex == lastIndex)
        }

        // Add Next button
        if (currentIndex < totalPages - 1) {
            items += PagingItem("Next", "index_${currentIndex + 1}.html", false)
        }

        return items
    }

    fun renderPage(
        post: BlogPost,
        recents: List<BlogFile>
        ){

        pageRenderer(
            categories = blogCategories,
            pages = blogPages,
            options = options,
            paging = emptyList(),
            config = config,
            pageFiles = emptyList(),
            recentsFiles = recents,
            templatePage = "post.html",
            fileName = post.link,
            post = post,
            navs = post.link.toBreadcrumbNavs()
        )

    }

    fun renderSubcategory(subCategory: Category){
        BllokGenerator(
            parentCategories = blogCategories,
            parentPages = blogPages,
            config = config,
            root = subCategory,
            isRoot = false,
            options = options
        ).generate()
    }

    fun renderRss(
        allFiles: List<BlogFile>
    ){

        val feed : List<BlogPost> = allFiles.recents(config.postsPerPage).map { it.parseBlogPost() }

        renderRssFeed(
            options = options,
            feed = feed,
            config = config
        )
    }

}


