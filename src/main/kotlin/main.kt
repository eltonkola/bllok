import com.eltonkola.bllok.Bllok
import java.time.Instant

fun main(args: Array<String>) {

    val startedAt = Instant.now()
    println("Bllok start!")
    if(args.size == 2) {
        val templatePath = args[0]
        val outputPath = args[1]
        println("templatePath: $templatePath")
        println("outputPath: $outputPath")
        Bllok(templatePath, outputPath).execute()
    }else{
        println("!!! please pass the template and output path sa parameters !!!")
    }
    println("Bllok end!")
    println("Executed in: ${Instant.now().toEpochMilli() - startedAt.toEpochMilli() } millisec!")

}
