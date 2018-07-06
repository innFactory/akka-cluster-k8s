package de.innfactory.akkaclusterk8s

import java.util.concurrent.TimeUnit

import akka.actor.{ActorSystem, Props}
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.pattern.ask
import akka.stream.ActorMaterializer
import akka.util.Timeout
import com.lightbend.rp.common.SocketBinding

import scala.concurrent.ExecutionContext

object ClusterApp extends App {
  implicit val actorSystem                     = ActorSystem("K8sCluster")
  implicit val executor: ExecutionContext      = actorSystem.dispatcher
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  val eventActor = actorSystem.actorOf(Props[SimpleClusterListener], name = "clusterListener")

  implicit val timeout = Timeout(5, TimeUnit.SECONDS)

  val routes =
    (get & pathEndOrSingleSlash) {
      complete("OK")
    } ~
      (
      path("events") {
        get {
          complete(
          (eventActor ? GetEvents()).mapTo[Result].map(template))
        }
      })


  val host = SocketBinding.bindHost("http", default = "localhost")
  val port = SocketBinding.bindPort("http", default = 8080)

  println(s"HTTP server available at http://$host:$port")
  Http().bindAndHandle(routes, host, port)

  private def template(result: Result): String =
    s"""|Akka Cluster Events
        |====================
        |
        |${result.events.mkString("\n")}""".stripMargin
}
