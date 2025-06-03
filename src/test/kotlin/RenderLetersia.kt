import com.eltonkola.Bllok
import com.eltonkola.model.BllokConfig
import java.time.Instant
import kotlin.system.exitProcess


fun main() {
    val startedAt = Instant.now()
    println("Bllok start!")
        Bllok(BllokConfig(
           // templatePath = SIMPLE_THEME,
            templatePath = MODERN_THEME,
            inputPath = "C:\\Users\\test\\Documents\\GitHub\\bllok\\demo_content\\letersia_blog\\blog_leterisa",
            outputPath = "C:\\Users\\test\\Documents\\GitHub\\bllok\\demo_content\\letersia_blog\\publish_blog_leterisa",
            debug = true //TODO - remove
        )).execute()
    println("Bllok end!")
    println("Executed in: ${Instant.now().toEpochMilli() - startedAt.toEpochMilli() } millisec!")
    exitProcess(0)
}
