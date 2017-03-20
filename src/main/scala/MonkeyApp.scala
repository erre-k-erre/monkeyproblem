import scala.io.StdIn

/**
  * Created by asantuy on 16/03/2017.
  */
object MonkeyApp extends App {
    val system = MonkeySystem()
    StdIn.readLine()
    system.terminate()
}
