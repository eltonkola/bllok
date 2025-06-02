package com.eltonkola.engine.rss

import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class RssFeedGenerator(
    private val title: String,
    private val link: String,
    private val description: String,
    private val feedUrl: String = link,
    private val language: String = "en-us"
) {
    data class RssItem(
        val title: String,
        val description: String,
        val link: String,
        val pubDate: ZonedDateTime = ZonedDateTime.now()
    )

    fun generate(items: List<RssItem>): String {
        val formatter = DateTimeFormatter.RFC_1123_DATE_TIME

        return buildString {
            appendLine("""<?xml version="1.0" encoding="UTF-8" ?>""")
            appendLine("""<rss version="2.0">""")
            appendLine("<channel>")
            appendLine("<title>${escape(title)}</title>")
            appendLine("<link>${escape(link)}</link>")
            appendLine("<description>${escape(description)}</description>")
            appendLine("<language>$language</language>")
            appendLine("<lastBuildDate>${formatter.format(ZonedDateTime.now())}</lastBuildDate>")
            appendLine("<docs>https://www.rssboard.org/rss-specification</docs>")
            appendLine("<generator>Simple Kotlin RSS Generator</generator>")

            for (item in items) {
                appendLine("<item>")
                appendLine("<title>${escape(item.title)}</title>")
                appendLine("<description>${escape(item.description)}</description>")
                appendLine("<link>${escape(item.link)}</link>")
                appendLine("<pubDate>${formatter.format(item.pubDate)}</pubDate>")
                appendLine("</item>")
            }

            appendLine("</channel>")
            appendLine("</rss>")
        }
    }

    private fun escape(input: String): String {
        return input.replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")
            .replace("'", "&apos;")
    }
}