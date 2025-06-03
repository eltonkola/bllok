import com.eltonkola.Bllok
import com.eltonkola.model.BllokConfig
import java.time.Instant
import kotlin.system.exitProcess


const val MODERN_THEME = "C:\\Users\\test\\Documents\\GitHub\\bllok\\demo_content\\themes\\modern_template"
const val SIMPLE_THEME = "C:\\Users\\test\\Documents\\GitHub\\bllok\\demo_content\\themes\\simple_template"

fun main() {
    val startedAt = Instant.now()
    println("Bllok start!")
        Bllok(BllokConfig(
            templatePath = SIMPLE_THEME,
//            templatePath = MODERN_THEME,
            inputPath = "C:\\Users\\test\\Documents\\GitHub\\bllok\\demo_content\\eltonkola_blog\\blog",
            outputPath = "C:\\Users\\test\\Documents\\GitHub\\bllok\\demo_content\\eltonkola_blog\\publish",
            debug = true //TODO - remove
        )).execute()
    println("Bllok end!")
    println("Executed in: ${Instant.now().toEpochMilli() - startedAt.toEpochMilli() } millisec!")
    exitProcess(0)
}
