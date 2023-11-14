package app
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.{Materializer, SystemMaterializer}
import route.Routes

import scala.concurrent.ExecutionContextExecutor

object Main {
  def main(args: Array[String]): Unit = {
    implicit val system: ActorSystem = ActorSystem("GameServerSystem")
    implicit val materializer: Materializer = SystemMaterializer(system).materializer

    implicit val executionContext: ExecutionContextExecutor = system.dispatcher
    val serverBindingFuture = Http().newServerAt("localhost", 8080).bind(Routes.routes)
  }
}
