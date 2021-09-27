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

        //copy res folder
        File("${Bllok.templatePath}res").copyRecursively(File("${Bllok.outputPath}res"), true)

    }

    private fun generateIndex(){
        val indexTemplate = openFile("${Bllok.templatePath}index.html")
        appData.articles.chunked(appData.config.postsPerPage).forEachIndexed { index, list ->
            val fileName = getIndexPageName(index)
            var pageContent = indexTemplate.generateCommonContent(fileName)
            //TODO - render articles here

            pageContent.findTagList(Tag.ARTICLES){ template ->
                //generate the categories menu code, based on the template
                var articlesContent = ""
                list.forEach { article ->
                    var currentArticle = template.replace(ContentTags.TITLE.tag, article.title)
                    currentArticle = currentArticle.replace(ContentTags.DESCRIPTION.tag, article.content.getSummary())
                    currentArticle = currentArticle.replace(ContentTags.PUB_DATE.tag, article.publicationDate.toReadableDate())
                    currentArticle = currentArticle.replace(ContentTags.CATEGORY.tag, article.label.map { it.name }.joinToString())
                    currentArticle = currentArticle.replace(ContentTags.HREF.tag, getArticlePageLink(article))

                    articlesContent += currentArticle
                }

                pageContent = pageContent.replace(template, articlesContent)
            }


            //TODO - render pagination here

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
                var menuItem = template.ifCurrentPageContent(currentPath, category)
                menuItem = menuItem.replace(ContentTags.TEXT.tag, category.name)
                menuItem = menuItem.replace(ContentTags.HREF.tag, getCategoryPageName(1, category))
                menuItem = menuItem.cleanTag(Tag.CATEGORIES)
                menuContent += menuItem
            }
            println("template: $template")
            println("menuContent: $menuContent")
            pageContent = pageContent.replace(template, menuContent)
        }

        //TODO - header metadata

        //TODO - footer icons and copyright

        return pageContent
    }

    private fun String.ifCurrentPageContent(currentPath : String, category: Label) : String {
        if(!this.contains(Tag.IF_CURRENT.open)){
            return this
        }
        val isSamePage = currentPath == getCategoryPageName(1, category)
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
