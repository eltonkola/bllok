import com.eltonkola.bllok.Bllok
import java.time.Instant
import kotlin.system.exitProcess

fun main(args: Array<String>) {

    val startedAt = Instant.now()
    println("Bllok start!")
    if(args.size == 5) {
        val templatePath = args[0]
        val outputPath = args[1]
        val githubToken = args[2]
        val owner = args[3]
        val repo = args[4]
        println("templatePath: $templatePath")
        println("outputPath: $outputPath")
        println("githubToken: $githubToken")
        println("owner: $owner")
        println("repo: $repo")
        Bllok(templatePath, outputPath, githubToken, owner, repo).execute()
    }else{
        println("!!! please pass the template and output path sa parameters !!!")
        exitProcess(1)
    }
    println("Bllok end!")
    println("Executed in: ${Instant.now().toEpochMilli() - startedAt.toEpochMilli() } millisec!")
    exitProcess(0)
}

