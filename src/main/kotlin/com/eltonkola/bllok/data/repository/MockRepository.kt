package com.eltonkola.bllok.data.repository

import com.eltonkola.bllok.data.model.Article
import com.eltonkola.bllok.data.model.Author
import com.eltonkola.bllok.data.model.Config
import com.eltonkola.bllok.data.model.Label
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZonedDateTime
import java.util.*

class MockRepository : DataRepository {

    private val elton = Author(
        username = "eltonkola",
        avatarUrl = "http",
        githubLink = "http://eltonkola.dev/",
    )

    private val categories = listOf(
        Label(
            id = 1,
            name = "Java",
            description = "Description category java",
            color = "#336699",
            default = false,
        ),
        Label(
            id = 1,
            name = "Kotlin",
            description = "Description category kotlin",
            color = "#336699",
            default = false,
        ),
        Label(
            id = 1,
            name = "Web",
            description = "Description category web",
            color = "#336699",
            default = false,
        ),
        Label(
            id = 1,
            name = "Other",
            description = "Description category other",
            color = "#336699",
            default = true,
        )
    )




    private val articles = listOf(
        Article(
            id = 1,
            title = "Article 1",
            content = "An h1 header\n" +
                    "============\n" +
                    "\n" +
                    "Paragraphs are separated by a blank line.\n" +
                    "\n" +
                    "2nd paragraph. *Italic*, **bold**, and `monospace`. Itemized lists\n" +
                    "look like:\n" +
                    "\n" +
                    "  * this one\n" +
                    "  * that one\n" +
                    "  * the other one\n" +
                    "\n" +
                    "Note that --- not considering the asterisk --- the actual text\n" +
                    "content starts at 4-columns in.\n" +
                    "\n" +
                    "> Block quotes are\n" +
                    "> written like so.\n" +
                    ">\n" +
                    "> They can span multiple paragraphs,\n" +
                    "> if you like.\n" +
                    "\n" +
                    "Use 3 dashes for an em-dash. Use 2 dashes for ranges (ex., \"it's all\n" +
                    "in chapters 12--14\"). Three dots ... will be converted to an ellipsis.\n" +
                    "Unicode is supported. ☺\n" +
                    "\n" +
                    "\n" +
                    "\n" +
                    "An h2 header\n" +
                    "------------\n" +
                    "\n" +
                    "Here's a numbered list:\n" +
                    "\n" +
                    " 1. first item\n" +
                    " 2. second item\n" +
                    " 3. third item\n" +
                    "\n" +
                    "Note again how the actual text starts at 4 columns in (4 characters\n" +
                    "from the left side). Here's a code sample:\n" +
                    "\n" +
                    "    # Let me re-iterate ...\n" +
                    "    for i in 1 .. 10 { do-something(i) }\n" +
                    "\n" +
                    "As you probably guessed, indented 4 spaces. By the way, instead of\n" +
                    "indenting the block, you can use delimited blocks, if you like:\n" +
                    "\n" +
                    "~~~\n" +
                    "define foobar() {\n" +
                    "    print \"Welcome to flavor country!\";\n" +
                    "}\n" +
                    "~~~\n" +
                    "\n" +
                    "(which makes copying & pasting easier). You can optionally mark the\n" +
                    "delimited block for Pandoc to syntax highlight it:\n" +
                    "\n" +
                    "~~~python\n" +
                    "import time\n" +
                    "# Quick, count to ten!\n" +
                    "for i in range(10):\n" +
                    "    # (but not *too* quick)\n" +
                    "    time.sleep(0.5)\n" +
                    "    print i\n" +
                    "~~~\n" +
                    "\n" +
                    "\n" +
                    "\n" +
                    "### An h3 header ###\n" +
                    "\n" +
                    "Now a nested list:\n" +
                    "\n" +
                    " 1. First, get these ingredients:\n" +
                    "\n" +
                    "      * carrots\n" +
                    "      * celery\n" +
                    "      * lentils\n" +
                    "\n" +
                    " 2. Boil some water.\n" +
                    "\n" +
                    " 3. Dump everything in the pot and follow\n" +
                    "    this algorithm:\n" +
                    "\n" +
                    "        find wooden spoon\n" +
                    "        uncover pot\n" +
                    "        stir\n" +
                    "        cover pot\n" +
                    "        balance wooden spoon precariously on pot handle\n" +
                    "        wait 10 minutes\n" +
                    "        goto first step (or shut off burner when done)\n" +
                    "\n" +
                    "    Do not bump wooden spoon or it will fall.\n" +
                    "\n" +
                    "Notice again how text always lines up on 4-space indents (including\n" +
                    "that last line which continues item 3 above).\n" +
                    "\n" +
                    "Here's a link to [a website](http://foo.bar), to a [local\n" +
                    "doc](local-doc.html), and to a [section heading in the current\n" +
                    "doc](#an-h2-header). Here's a footnote [^1].\n" +
                    "\n" +
                    "[^1]: Footnote text goes here.\n" +
                    "\n" +
                    "Tables can look like this:\n" +
                    "\n" +
                    "size  material      color\n" +
                    "----  ------------  ------------\n" +
                    "9     leather       brown\n" +
                    "10    hemp canvas   natural\n" +
                    "11    glass         transparent\n" +
                    "\n" +
                    "Table: Shoes, their sizes, and what they're made of\n" +
                    "\n" +
                    "(The above is the caption for the table.) Pandoc also supports\n" +
                    "multi-line tables:\n" +
                    "\n" +
                    "--------  -----------------------\n" +
                    "keyword   text\n" +
                    "--------  -----------------------\n" +
                    "red       Sunsets, apples, and\n" +
                    "          other red or reddish\n" +
                    "          things.\n" +
                    "\n" +
                    "green     Leaves, grass, frogs\n" +
                    "          and other things it's\n" +
                    "          not easy being.\n" +
                    "--------  -----------------------\n" +
                    "\n" +
                    "A horizontal rule follows.\n" +
                    "\n" +
                    "***\n" +
                    "\n" +
                    "Here's a definition list:\n" +
                    "\n" +
                    "apples\n" +
                    "  : Good for making applesauce.\n" +
                    "oranges\n" +
                    "  : Citrus!\n" +
                    "tomatoes\n" +
                    "  : There's no \"e\" in tomatoe.\n" +
                    "\n" +
                    "Again, text is indented 4 spaces. (Put a blank line between each\n" +
                    "term/definition pair to spread things out more.)\n" +
                    "\n" +
                    "Here's a \"line block\":\n" +
                    "\n" +
                    "| Line one\n" +
                    "|   Line too\n" +
                    "| Line tree\n" +
                    "\n" +
                    "and images can be specified like so:\n" +
                    "\n" +
                    "![example image](example-image.jpg \"An exemplary image\")\n" +
                    "\n" +
                    "Inline math equations go in like so: \$\\omega = d\\phi / dt\$. Display\n" +
                    "math should get its own line and be put in in double-dollarsigns:\n" +
                    "\n" +
                    "\$I = \\int \\rho R^{2} dV\$\$\n" +
                    "\n" +
                    "And note that you can backslash-escape any punctuation characters\n" +
                    "which you wish to be displayed literally, ex.: \\`foo\\`, \\*bar\\*, etc."
            ,
            publicationDate = ZonedDateTime.now(),
            updateDate = ZonedDateTime.now(),
            author = elton,
            label = listOf(categories.random())
        ),
        Article(
            id = 2,
            title = "Article 2",
            content =
                        "Lorem ipsum dolor sit amet consectetur adipisicing elit. Maxime mollitia,\n" +
                        "molestiae quas vel sint commodi repudiandae consequuntur voluptatum laborum\n" +
                        "numquam blanditiis harum quisquam eius sed odit fugiat iusto fuga praesentium\n" +
                        "optio, eaque rerum! Provident similique accusantium nemo autem. Veritatis\n" +
                        "obcaecati tenetur iure eius earum ut molestias architecto voluptate aliquam\n" +
                        "nihil, eveniet aliquid culpa officia aut! Impedit sit sunt quaerat, odit,\n" +
                        "tenetur error, harum nesciunt ipsum debitis quas aliquid. Reprehenderit,\n" +
                        "quia. Quo neque error repudiandae fuga? Ipsa laudantium molestias eos \n" +
                        "sapiente officiis modi at sunt excepturi expedita sint? Sed quibusdam\n" +
                        "recusandae alias error harum maxime adipisci amet laborum. Perspiciatis \n" +
                        "minima nesciunt dolorem! Officiis iure rerum voluptates a cumque velit \n" +
                        "quibusdam sed amet tempora. Sit laborum ab, eius fugit doloribus tenetur \n" +
                        "fugiat, temporibus enim commodi iusto libero magni deleniti quod quam \n" +
                        "consequuntur! Commodi minima excepturi repudiandae velit hic maxime\n" +
                        "doloremque. Quaerat provident commodi consectetur veniam similique ad \n" +
                        "earum omnis ipsum saepe, voluptas, hic voluptates pariatur est explicabo \n" +
                        "fugiat, dolorum eligendi quam cupiditate excepturi mollitia maiores labore \n" +
                        "suscipit quas? Nulla, placeat. Voluptatem quaerat non architecto ab laudantium\n" +
                        "modi minima sunt esse temporibus sint culpa, recusandae aliquam numquam \n" +
                        "totam ratione voluptas quod exercitationem fuga. Possimus quis earum veniam \n" +
                        "quasi aliquam eligendi, placeat qui corporis!"
            ,
            publicationDate = ZonedDateTime.now(),
            updateDate = ZonedDateTime.now(),
            author = elton,
            label = listOf(categories.random())
        )
    )


    override fun getArticles(page: Int): List<Article> {
        if(page > 1){
            return emptyList()
        }
        return articles
    }

    override fun getConfig(): Config {
        return Config()
    }

}
