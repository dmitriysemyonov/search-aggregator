package el.goog.aggregator.web


import java.time.LocalTime
import java.time.format.DateTimeFormatter.ISO_LOCAL_TIME

import akka.NotUsed
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshalling.ToResponseMarshallable._
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.sse.ServerSentEvent
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Source
import el.goog.aggregator.dto._
import spray.json.DefaultJsonProtocol.{jsonFormat3, _}
import spray.json.RootJsonFormat

import scala.concurrent.ExecutionContextExecutor
import scala.concurrent.duration._
import scala.io.StdIn

object Server {
  var sequence: Int = 0

  implicit val system: ActorSystem = ActorSystem("my-system")
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher
  implicit val materializer: ActorMaterializer = ActorMaterializer()


  /**
    * Application entry point.
    *
    * @param args
    */
  def main(args: Array[String]) {
    implicit val taskFormat: RootJsonFormat[Task] = jsonFormat3(Task)

    import akka.http.scaladsl.marshalling.sse.EventStreamMarshalling._
    import el.goog.aggregator.dto.ResponseJsonSupport._

    val route: Route =
      get {
        path("diag") {
          complete(HttpEntity(ContentTypes.`text/plain(UTF-8)`, "ok"))
        }
      } ~
        post {
          path("task") {
            entity(as[Task]) { task =>
              //todo: return future
              complete(submitTask(task))
            }
          }
        } ~
        get {
          path("results") {
            complete {
              Source
                .tick(2.seconds, 2.seconds, NotUsed)
                .map(_ => LocalTime.now())
                .map(time => ServerSentEvent(ISO_LOCAL_TIME.format(time)))
                .keepAlive(1.second, () => ServerSentEvent.heartbeat)
            }
          }
        }


    val bindingFuture = Http().bindAndHandle(route, "localhost", 8080)

    StdIn.readLine()
    bindingFuture
      .flatMap(_.unbind())
      .onComplete(_ => system.terminate())

  }

  private def submitTask(task: Task) = {
    sequence = sequence + 1
    Response(sequence)
  }
}
