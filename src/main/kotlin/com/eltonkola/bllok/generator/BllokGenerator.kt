package com.eltonkola.bllok.generator

import com.eltonkola.bllok.Bllok
import com.eltonkola.bllok.data.model.AppData
import com.eltonkola.bllok.data.model.Label
import java.io.File

class BllokGenerator(val appData: AppData){

    fun generate(){

        //clean the output folder
        cleanDirectory(Bllok.outputPath)

        //generate content
        generateIndex()
        generateCategories()
        generateArticles()

        //generate rss
        RssFeedGenerator().generateRssFeed(appData)

        //copy res folder
        File("${Bllok.templatePath}res").copyRecursively(File("${Bllok.outputPath}res"), true)

    }

    private fun generateIndex(){
        val indexTemplate = openFile("${Bllok.templatePath}index.html")
        appData.articles.chunked(appData.config.postsPerPage).forEachIndexed { index, list ->
            val fileName = getIndexPageName(index)
            var pageContent = indexTemplate.generateCommonContent(fileName)

            //render articles here
            pageContent.findTagList(Tag.ARTICLES){ template ->
                //generate the categories menu code, based on the template
                var articlesContent = ""
                list.forEach { article ->
                    var currentArticle = template.replace(ContentTags.TITLE.tag, article.title)
                    currentArticle = currentArticle.replace(ContentTags.DESCRIPTION.tag, article.content.getSummary())
                    currentArticle = currentArticle.replace(ContentTags.PUB_DATE.tag, article.publicationDate.toReadableDate())
                    currentArticle = currentArticle.replace(ContentTags.CATEGORY.tag, article.label.joinToString { it.name })
                    currentArticle = currentArticle.replace(ContentTags.HREF.tag, getArticlePageName(article))

                    articlesContent += currentArticle
                }

                pageContent = pageContent.replace(template, articlesContent)
            }
            //render pagination here
            pageContent.findTagList(Tag.PAGING_PAGES){ template ->
                //we will render all
                var pagingContent = ""

                val nrPages = (appData.articles.size / appData.config.postsPerPage) - 1
                (0..nrPages).forEach {
                    var menuItem = template.ifCurrentPageContent(fileName, it)
                    menuItem = menuItem.replace(ContentTags.TEXT.tag, it.toString())
                    menuItem = menuItem.replace(ContentTags.HREF.tag, getIndexPageName(it))
                    menuItem = menuItem.cleanTag(Tag.CATEGORIES)
                    pagingContent += menuItem
                }

                pageContent = pageContent.replace(template, pagingContent)
            }
            //show next and previous page buttons
            pageContent.findTagList(Tag.PREVIOUS_PAGE){ template ->
                //we will render all
                var pagingContent = ""
                if(index > 0){
                    pagingContent += template.replace(ContentTags.HREF.tag, getIndexPageName(index -1))
                }
                pageContent = pageContent.replace(template, pagingContent)
            }

            pageContent.findTagList(Tag.NEXT_PAGE){ template ->
                //we will render all
                val nrPages = (appData.articles.size / appData.config.postsPerPage) - 1
                var pagingContent = ""
                if(index < nrPages){
                    pagingContent += template.replace(ContentTags.HREF.tag, getIndexPageName(index + 1))
                }
                pageContent = pageContent.replace(template, pagingContent)
            }

            saveFile("${Bllok.outputPath}${fileName}", pageContent)
        }

    }

    private fun generateCategories(){
        val indexTemplate = openFile("${Bllok.templatePath}category.html")
        appData.labels.forEach { category ->

            appData.articles.filter { it.label.contains(category) }
                .chunked(appData.config.postsPerPage).forEachIndexed { index, list ->

                val fileName = getCategoryPageName(index, category)
                var pageContent = indexTemplate.generateCommonContent(fileName)


                pageContent = pageContent.replace(ContentTags.CATEGORY.tag, category.name)
                pageContent = pageContent.replace(ContentTags.DESCRIPTION.tag, category.description)

                //render articles here
                pageContent.findTagList(Tag.ARTICLES){ template ->
                    //generate the categories menu code, based on the template
                    var articlesContent = ""
                    list.forEach { article ->
                        var currentArticle = template.replace(ContentTags.TITLE.tag, article.title)
                        currentArticle = currentArticle.replace(ContentTags.DESCRIPTION.tag, article.content.getSummary())
                        currentArticle = currentArticle.replace(ContentTags.PUB_DATE.tag, article.publicationDate.toReadableDate())
                        currentArticle = currentArticle.replace(ContentTags.CATEGORY.tag, article.label.map { it.name }.joinToString())
                        currentArticle = currentArticle.replace(ContentTags.HREF.tag, getArticlePageName(article))

                        articlesContent += currentArticle
                    }

                    pageContent = pageContent.replace(template, articlesContent)
                }
                //render pagination here
                pageContent.findTagList(Tag.PAGING_PAGES){ template ->
                    //we will render all
                    var pagingContent = ""

                    val nrPages = (appData.articles.size / appData.config.postsPerPage) - 1
                    (0..nrPages).forEach {
                        var menuItem = template.ifCurrentCategoryPageContentPager(fileName, category, it)
                        menuItem = menuItem.replace(ContentTags.TEXT.tag, it.toString())
                        menuItem = menuItem.replace(ContentTags.HREF.tag, getCategoryPageName(it, category))
                        menuItem = menuItem.cleanTag(Tag.CATEGORIES)
                        pagingContent += menuItem
                    }

                    pageContent = pageContent.replace(template, pagingContent)
                }
                //show next and previous page buttons
                pageContent.findTagList(Tag.PREVIOUS_PAGE){ template ->
                    //we will render all
                    var pagingContent = ""
                    if(index > 0){
                        pagingContent += template.replace(ContentTags.HREF.tag, getCategoryPageName(index -1, category))
                    }
                    pageContent = pageContent.replace(template, pagingContent)
                }

                pageContent.findTagList(Tag.NEXT_PAGE){ template ->
                    //we will render all
                    val nrPages = (appData.articles.size / appData.config.postsPerPage) - 1
                    var pagingContent = ""
                    if(index < nrPages){
                        pagingContent += template.replace(ContentTags.HREF.tag, getCategoryPageName(index + 1, category))
                    }
                    pageContent = pageContent.replace(template, pagingContent)
                }

                saveFile("${Bllok.outputPath}${fileName}", pageContent)
            }
        }

    }

    private fun generateArticles(){
        val articleTemplate = openFile("${Bllok.templatePath}article.html")
        appData.articles.forEach { article ->

            val fileName = getArticlePageName(article)
            var pageContent = articleTemplate.generateCommonContent(fileName)

            pageContent = pageContent.replace(ContentTags.TITLE.tag, article.title)
            pageContent = pageContent.replace(ContentTags.DESCRIPTION.tag, article.content.toHtml())
            pageContent = pageContent.replace(ContentTags.PUB_DATE.tag, article.publicationDate.toReadableDate())
            pageContent = pageContent.replace(ContentTags.CATEGORY.tag, article.label.joinToString { it.name })
            pageContent = pageContent.replace(ContentTags.HREF.tag, getArticlePageName(article))

            saveFile("${Bllok.outputPath}${fileName}", pageContent)
        }

    }






    private fun String.generateCommonContent(currentPath : String) : String {

        var pageContent = this
        //categories links

        this.findTagList(Tag.CATEGORIES){ template ->
            //generate the categories menu code, based on the template
            var menuContent = ""
            appData.labels.forEach { category ->
                var menuItem = template.ifCurrentCategoryPageContent(currentPath, category)
                menuItem = menuItem.replace(ContentTags.TEXT.tag, category.name)
                menuItem = menuItem.replace(ContentTags.HREF.tag, getCategoryPageName(0, category))
                menuItem = menuItem.cleanTag(Tag.CATEGORIES)
                menuContent += menuItem
            }
           // println("template: $template")
           // println("menuContent: $menuContent")
            pageContent = pageContent.replace(template, menuContent)
        }

        //header metadata
        pageContent = pageContent.replace(ContentTags.HEADER_TITLE.tag, appData.config.websiteName)
        pageContent = pageContent.replace(ContentTags.HEADER_CREATOR.tag, appData.config.feedEmailRealName)
        pageContent = pageContent.replace(ContentTags.HEADER_DESCRIPTION.tag, appData.config.websiteDescription)
        pageContent = pageContent.replace(ContentTags.HEADER_URL.tag, appData.config.webUrl)
        pageContent = pageContent.replace(ContentTags.HEADER_PERPAGE.tag, appData.config.postsPerPage.toString())

        //TODO - footer icons and copyright
        pageContent = pageContent.replace(ContentTags.FOOTER_TWITTER.tag, appData.config.twitter)
        pageContent = pageContent.replace(ContentTags.FOOTER_GITHUB.tag, appData.config.github)
        pageContent = pageContent.replace(ContentTags.FOOTER_COPYRIGHT.tag, appData.config.copyright)

        return pageContent
    }

    private fun String.ifCurrentCategoryPageContent(currentPath : String, category: Label) : String {
        if(!this.contains(Tag.IF_CURRENT.open)){
            return this
        }
        val categoryPath = getCategoryPageName(0, category)
        val isSamePage = currentPath == categoryPath ||
                currentPath.replace(".html","").contains(categoryPath.replace(".html",""))

        return if(isSamePage){
            print("if $categoryPath == $currentPath \n")
            this.between(Tag.IF_CURRENT.open, Tag.IF_CURRENT.conditional!!)
        }else{
            this.between(Tag.IF_CURRENT.conditional!!, Tag.IF_CURRENT.close)
        }
    }

    private fun String.ifCurrentCategoryPageContentPager(currentPath : String, category: Label, page: Int) : String {
        if(!this.contains(Tag.IF_CURRENT.open)){
            return this
        }
        val categoryPath = getCategoryPageName(page, category)
        val isSamePage = currentPath == categoryPath ||
                if(page == 0) false else currentPath.replace(".html","").contains(categoryPath.replace(".html",""))

        return if(isSamePage){
            print("if $categoryPath == $currentPath \n")
            this.between(Tag.IF_CURRENT.open, Tag.IF_CURRENT.conditional!!)
        }else{
            this.between(Tag.IF_CURRENT.conditional!!, Tag.IF_CURRENT.close)
        }
    }

    private fun String.ifCurrentPageContent(currentPath : String, page: Int) : String {
        if(!this.contains(Tag.IF_CURRENT.open)){
            return this
        }
        val indexPath = getIndexPageName(page)
        val isSamePage = currentPath == getIndexPageName(page) ||
               if(page == 0) false else currentPath.replace(".html","").contains(indexPath.replace(".html",""))
        return if(isSamePage){
            this.between(Tag.IF_CURRENT.open, Tag.IF_CURRENT.conditional!!)
        }else{
            this.between(Tag.IF_CURRENT.conditional!!, Tag.IF_CURRENT.close)
        }
    }

    private fun String.between(start:String, end:String): String {
        return this.substring(this.indexOf(start) + start.length, this.indexOf(end))
    }


}
