package com.eltonkola

import com.eltonkola.engine.pageRenderer
import com.eltonkola.engine.recents
import com.eltonkola.model.BllokConfig
import com.eltonkola.model.BlogConfig
import com.eltonkola.model.BlogPost
import com.eltonkola.model.Category
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
        //1. generate index page
        renderIndex()
        //2. generate every page
        val pages = root.getPosts()
        pages.forEach { renderPage(it) }
        //3. generate every subcategory)
        root.subcategories.forEach { renderSubcategory(it) }
        //4. only if root, crete a rss feed
        if(isRoot){
            renderRss()
        }

    }

    fun renderIndex(){
        //make directory if it does not exist
        val currentDir = File(options.outputPath, root.path)
        currentDir.mkdirs()
        //scan all docs in the tree, and generate a list of X last posts

        val allFiles = root.getAllFiles().sortedBy { it.lastModified() }
        val pages = allFiles.chunked(config.postsPerPage)
        print("pages: $pages")
        val recents = allFiles.recents(5)
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

    fun renderPage(post: BlogPost){

    }

    fun renderSubcategory(subCategory: Category){

    }

    fun renderRss(){

    }

}
