package au.id.fsat.examples

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import scopt.OptionParser

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.io.StdIn

/**
 * Main entry point, accepts user argument from scopt then starts a simple web endpoint.
 */
object Main {
  case class InputArgs(host: Option[String] = None, port: Option[Int] = None)

  val parser = new OptionParser[InputArgs]("example") {
    head("Example app")

    version("Example app")

    opt[String]("host")
      .text("Specifies the web endpoint bind host")
      .action((v, args) => args.copy(host = Some(v)))

    opt[Int]("port")
      .text("Specifies the web endpoint bind port")
      .action((v, args) => args.copy(port = Some(v)))
  }

  def startWebServer(host: String, port: Int): Unit = {
    implicit val system = ActorSystem("my-system")
    implicit val materializer = ActorMaterializer()
    // needed for the future flatMap/onComplete in the end
    implicit val executionContext = system.dispatcher

    val route = path("") {
      get {
        complete(HttpEntity(ContentTypes.`text/plain(UTF-8)`, "TEST"))
      }
    }

    val bound = Http().bindAndHandle(route, host, port)

    println(s"\n\nHTTP ENDPOINT RUNNING AT $host:$port\nPress Enter to exit\n\n")

    StdIn.readLine()

    Await.ready(
      bound
        .flatMap(_.unbind())
        .flatMap(_ => system.terminate()),
      1.minute)
  }

  def main(args: Array[String]): Unit = {
    parser.parse(args, InputArgs()) match {
      case Some(InputArgs(Some(host), Some(port))) => startWebServer(host, port)
      case _                                       => sys.exit(1)
    }
  }
}
