package com.eltonkola.model

import java.io.File


data class BlogFile(
    val parent: Category ?= null,
    val file: File
)
data class Category(
    val directory: String,
    val name: String,
    val path: String,
    val subcategories: List<Category> = emptyList(),
    val files : List<BlogFile>
){
    fun getPosts() : List<BlogPost> {
        val posts = files.map { it.parseBlogPost() }
        return posts
    }

    fun printTree(parent: String = "") {
        println("$parent-----")
        println("$parent name: $name")
        println("$parent directory : $directory")
        println("$parent path : $path")
        println("$parent subcategories : ${subcategories.size}")
        subcategories.forEach { it.printTree("$parent>>") }
    }

    //For now, we will sort by file os date, not metadata o the file, that would be too expensive
    fun getAllFiles() : List<BlogFile> {
        val childFiles = subcategories.map { it.getAllFiles() }.flatten()
        return listOf(files, childFiles).flatten()
    }
}

fun buildCategoryTree(
    directory: File,
    root: File = directory,
    parent: Category? = null
): Category {
    val subdirs = directory.listFiles()?.filter { it.isDirectory } ?: emptyList()
    val files = directory.listFiles()?.filter { it.extension == "md" }?.map {
        BlogFile(parent = parent, file = it)
    } ?: emptyList()


    val category = Category(
        directory = directory.path,
        name = directory.name,
        path = directory.relativeTo(root).path.replace(File.separatorChar, '/'),
        files = files,
        subcategories = emptyList()
    )


    return category.copy(
        subcategories = subdirs.map { buildCategoryTree(it, root = root , parent = category)},
        files = files.map {
            it.copy(parent = category)
        }
        )
}
