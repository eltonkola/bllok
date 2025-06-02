package com.eltonkola

import com.eltonkola.engine.pageRenderer
import com.eltonkola.engine.recents
import com.eltonkola.engine.rss.renderRssFeed
import com.eltonkola.model.BllokConfig
import com.eltonkola.model.BlogConfig
import com.eltonkola.model.BlogPost
import com.eltonkola.model.Category
import com.eltonkola.model.parseBlogPost
import java.io.File

class BllokGenerator(
    private val config: BlogConfig,
    private val root: Category,
    private val isRoot: Boolean,
    val options: BllokConfig
){
    fun generate(){
        //we will generate a category, this may be the root folder, or a subfolder on a tree of content
        println("config: $config")
        println("inputPath: $root")

        val allFiles = root.getAllFiles().sortedBy { it.lastModified() }
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
        allFiles: List<File>,
        recents: List<File>
    ){
        //make directory if it does not exist
        val currentDir = File(options.outputPath, root.path)
        currentDir.mkdirs()
        //scan all docs in the tree, and generate a list of X last posts

        val pages = allFiles.chunked(config.postsPerPage)
        print("pages: $pages")

        pages.forEachIndexed { index, page ->
            print("index: $index - page: $page")
            val fileName = if(index == 0) "index.html" else "index_${index+1}.html"
            pageRenderer(
                options = options,
                config = config,
                pageFiles = page,
                recentsFiles = recents,
                templatePage = "index.html",
                fileName = root.path + fileName
            )
        }

    }

    fun renderPage(
        post: BlogPost,
        recents: List<File>
        ){

    }

    fun renderSubcategory(subCategory: Category){
//        BllokGenerator(
//            config = config,
//            root = subCategory,
//            isRoot = false,
//            options = options
//        ).generate()
    }

    fun renderRss(
        allFiles: List<File>
    ){

        val feed : List<BlogPost> = allFiles.recents(config.postsPerPage).map { parseBlogPost(it.path) }

        renderRssFeed(
            options = options,
            feed = feed,
            config = config
        )
    }

}
