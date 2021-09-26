import com.eltonkola.bllok.Bllok
import java.time.Instant

fun main(args: Array<String>) {

    val startedAt = Instant.now()
    println("Bllok start!")
    args.forEach {
        println("Param: $it")
    }
    Bllok().execute()
    println("Bllok end!")
    println("Executed in: ${Instant.now().toEpochMilli() - startedAt.toEpochMilli() } millisec!")

}
