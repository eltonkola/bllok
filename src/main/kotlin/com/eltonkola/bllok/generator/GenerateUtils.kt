package com.eltonkola.bllok.generator

enum class Tags(val open: String, val close: String){
    INCLUDE("<include>","</include>"),
    ARTICLES("<articles>","</articles>"),
    CATEGORIES("<categories>","</categories>"),
    PAGING_PAGES("<paging>","</paging>"),
}

enum class ContentTags(val tag: String){
    TITLE("\$title"),
    DESCRIPTION("\$description"),
    TEXT("\$text"),
    HREF("\$href"),
    PUB_DATE("\$pub_date"),
    CATEGORY("\$category"),
}
