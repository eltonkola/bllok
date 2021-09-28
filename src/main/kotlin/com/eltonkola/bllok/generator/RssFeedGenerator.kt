package com.eltonkola.bllok.generator

import com.eltonkola.bllok.Bllok
import com.eltonkola.bllok.data.model.AppData
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class RssFeedGenerator {

    fun generateRssFeed(appData: AppData){
         val formatter =  DateTimeFormatter.RFC_1123_DATE_TIME
        val limit = if(appData.articles.size> 30) 30 else appData.articles.size
        val articles = appData.articles.subList(0, limit).map {
                    "            <item>\n" +
                    "                <title>${it.title}</title>\n" +
                    "                <description>" +
                    "                   <![CDATA[${it.content.toHtml()}]]>" +
                    "                </description>\n" +
                    "                <link>${appData.config.webUrl}${getArticlePageName(it)}</link>\n" +
                    "                <category domain=\"${appData.config.webUrl}\">${it.label.joinToString("/") { it.name }}</category>\n" +
                    "                <comments>${appData.config.webUrl}</comments>\n" +
                    "                <pubDate>${formatter.format(it.publicationDate)}</pubDate>\n" +
                    "                <guid>${appData.config.webUrl}${getArticlePageName(it)}</guid>\n" +
                    "            </item>\n"
        }

        val feedContent = "<rss version=\"2.0\">\n" +
                "        <channel>\n" +
                "            <title>${appData.config.websiteName}</title>\n" +
                "            <description>${appData.config.websiteDescription}</description>\n" +
                "            <link>${appData.config.webUrl}</link>\n" +
                "            <category domain=\"${appData.config.webUrl}\">${appData.labels.joinToString("/") { it.name }}</category>\n" +
                "            <copyright>${appData.config.copyright}</copyright>\n" +
                "            <docs>${appData.config.webUrl}feed.rss</docs>\n" +
                "            <language>en-us</language>\n" +
                "            <lastBuildDate>${formatter.format(ZonedDateTime.now())}</lastBuildDate>\n" +
                "            <managingEditor>${appData.config.feedEmail} (${ (appData.config.feedEmailRealName)})</managingEditor>\n" +
                "            <pubDate>${formatter.format(ZonedDateTime.now())}</pubDate>\n" +
                "            <webMaster>${appData.config.feedEmail} (${ (appData.config.feedEmailRealName)})</webMaster>\n" +
                "            <generator>Bllok ${Bllok.version}</generator>\n" +
                "            <image>\n" +
                "                <url>${appData.config.webUrl}res/apple-touch-icon.png</url>\n" +
                "                <title>${appData.config.websiteName}</title>\n" +
                "                <link>${appData.config.webUrl}</link>\n" +
                "                <description>${appData.config.websiteName}</description>\n" +
                "                <width>144</width>\n" +
                "                <height>144</height>\n" +
                "            </image>\n" +
                "            \n" +
                                articles.joinToString("\n") +
                "            \n" +
                "    </channel>\n" +
                "    </rss>"
        saveFile("${Bllok.outputPath}feed.xml", feedContent)
    }


}
