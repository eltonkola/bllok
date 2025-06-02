package com.eltonkola.engine.rss

import com.eltonkola.model.BllokConfig
import com.eltonkola.model.BlogConfig
import com.eltonkola.model.BlogPost
import java.io.File

fun renderRssFeed(
    options: BllokConfig,
    feed : List<BlogPost>,
    config: BlogConfig,
) {
    val generator = RssFeedGenerator(
        title = config.websiteName,
        link = config.baseUrl,
        description = config.websiteDescription
    )

    val items = feed.map { post ->
        RssFeedGenerator.RssItem(
            title = post.metadata.title,
            description = post.snippet,
            link = "${config.baseUrl}/${post.fileName}",
        )
    }

    val rssXml = generator.generate(items)
    println(rssXml)

    File(options.outputPath,"rss.xml").writeText(rssXml)
}
