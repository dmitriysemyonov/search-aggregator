package el.goog.aggregator.web

import akka.actor.{ActorSystem, Props}
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshalling.ToResponseMarshallable._
import akka.http.scaladsl.model.sse.ServerSentEvent
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.scaladsl.{Concat, Source}
import akka.stream.{ActorMaterializer, ThrottleMode}
import akka.util.Timeout
import el.goog.aggregator.persistence.{ProductionDb, Search}
import el.goog.aggregator.search.{PersistentSearchActor, SearchEngineGate, Sequence}
import el.goog.aggregator.util.{Conf, Log}
import el.goog.aggregator.web.dto._
import org.joda.time.DateTime
import spray.json.{JsonWriter, RootJsonFormat}

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContextExecutor, Future}
import scala.io.StdIn
import scala.util.{Failure, Success}


/**
  * Application entry point.
  */
object Server extends App with Log {
  log.info("Starting Server")

  val db = ProductionDb
  db.autoCreate()

  import akka.http.scaladsl.marshalling.sse.EventStreamMarshalling._
  import el.goog.aggregator.web.dto.ResponseJsonSupport._

  implicit val system: ActorSystem = ActorSystem("search-aggregator")
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  implicit val taskFormat: RootJsonFormat[Task] = jsonFormat3(Task)
  implicit val resultFormat: RootJsonFormat[Result] = jsonFormat2(Result)

  SearchEngineGate.loadSearchEngines()

  val route: Route =
    post {
      path("task") {
        entity(as[Task]) { task =>
          complete(submitTask(task))
        }
      }
    } ~
      get {
        path("results") {
          parameters('id.as[Int]) {
            id =>
              complete {
                source(id).map(wrapWithServerSentEvent(_))
              }
          }
        }
      }


  val bindingFuture = Http().bindAndHandle(route, "localhost", Conf.port)

  StdIn.readLine()
  bindingFuture
    .flatMap(_.unbind())
    .onComplete(_ => system.terminate())


  private def wrapWithServerSentEvent[T](element: T)(implicit writer: JsonWriter[T]): ServerSentEvent =
    ServerSentEvent(writer.write(element).compactPrint, "result")

  private def source(since: Int) = {
    Source
      .combine(dbSource(since), pollChangesSource(since))(Concat[Result])
  }

  private def dbSource(since: Int) = {
    Source.fromFuture(mapFutures(db.getSearchIdsGt(since))).flatMapConcat(list => Source.fromIterator(() => list.iterator))
  }

  private def toResult(x: Search) = Result(x.id, x.result)

  private def mapFutures(xs: Future[List[Search]]) = {
    for (list <- xs) yield list map toResult
  }

  private def pollChangesSource(since: Int) = {
    val lastModified = DateTime.now()
    Source
      .unfoldAsync(lastModified) { timestamp =>

        db.getSearchIdGtLastModifiedGt(since, lastModified)
          .map(result => Some((DateTime.now(), result map toResult)))
      }
      .throttle(1, 5 second, 1, ThrottleMode.Shaping)
      .flatMapConcat { results =>
        Source.fromIterator(() => results.iterator)
      }
  }

  private def submitTask(task: Task) = {
    //todo: use persistent sequence
    val id = Sequence.next()
    val search = Search(
      id = id,
      term = task.term,
      priority = task.priority,
      num = task.num
    )
    db.store(search)

    implicit val timeout: Timeout = Timeout(1 second)
    system.actorSelection(s"akka://search-aggregator/user/search-$id").resolveOne().onComplete {
      case Success(_) =>
        log.info(s"Search with id=$id already requested")
      //execute new search on old instance?
      //nah

      case Failure(_) =>
        log.info(s"Creating new search actor for search id=$id")
        val actorRef = system.actorOf(Props[PersistentSearchActor], s"search-$id")
        //todo: use priority queue for search actor mailbox
        actorRef ! search
    }

    Response(id)
  }
}
