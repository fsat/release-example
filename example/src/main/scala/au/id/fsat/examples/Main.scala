/*
 * Copyright 2018 Felix Satyaputra
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package au.id.fsat.examples

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import scopt.OptionParser

import scala.concurrent.{ Await, ExecutionContext }
import scala.concurrent.duration._
import scala.io.StdIn

/**
 * Main entry point, accepts user argument from scopt then starts a simple web endpoint.
 */
object Main {
  case class InputArgs(host: Option[String] = None, port: Option[Int] = None)

  val parser = new OptionParser[InputArgs]("example") {
    head(s"Example app ${BuildInfo.version}")

    version("version")
      .text("Shows the version of the application")

    help("help")
      .text("Shows help text")

    opt[String]("host")
      .text("Specifies the web endpoint bind host")
      .action((v, args) => args.copy(host = Some(v)))

    opt[Int]("port")
      .text("Specifies the web endpoint bind port")
      .action((v, args) => args.copy(port = Some(v)))
  }

  private def startWebServer(host: String, port: Int): Unit = {
    implicit val system: ActorSystem = ActorSystem("my-system")
    implicit val materializer: ActorMaterializer = ActorMaterializer()
    // needed for the future flatMap/onComplete in the end
    implicit val executionContext: ExecutionContext = system.dispatcher

    val route = path("") {
      get {
        complete(HttpEntity(ContentTypes.`text/plain(UTF-8)`, "TEST\n\n"))
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

  private def showHelpText(): Unit = parser.parse(Seq("--help"), InputArgs())

  def main(args: Array[String]): Unit = {
    parser.parse(args, InputArgs()) match {
      case Some(InputArgs(Some(host), Some(port))) => startWebServer(host, port)
      case _ =>
        showHelpText()
        sys.exit(1)
    }
  }
}
