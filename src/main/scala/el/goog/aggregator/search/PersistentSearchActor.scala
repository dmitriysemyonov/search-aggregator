package el.goog.aggregator.search

import akka.actor._
import akka.persistence._
import el.goog.aggregator.persistence.{ProductionDb, Search}
import el.goog.aggregator.util.Log


sealed trait Event

case class TraceEvent(data: String) extends Event

case class RequestEvent(search: Search) extends Event

case class ResultEvent(result: Result) extends Event

//sealed trait Command
//case class SearchResult(id: Int, result: List[String]) extends Command
//case class Response(id: Int) extends Command


case class SearchState(search: Search = null, events: List[Event] = Nil) {
  def updated(event: Event): SearchState = event match {
    case evt: TraceEvent => copy(events = evt :: events)
    case evt: RequestEvent => SearchState(search = evt.search)
    case evt: ResultEvent => copy(search.copy(result = search.result ++ evt.result.result))
  }
}


class PersistentSearchActor extends PersistentActor with Log {
  override def persistenceId = "search"

  private val db = ProductionDb
  var state = SearchState()

  def updateState(event: Event): Unit =
    state = state.updated(event)

  log.debug(s"updated state: $state")

  def numEvents: Int =
    state.events.size

  val receiveRecover: Receive = {
    case evt: Event => updateState(evt)
    case SnapshotOffer(_, snapshot: SearchState) => state = snapshot
  }

  val receiveCommand: Receive = {
    case search: Search =>
      log.info(s"Search actor: receive request: ${search.term}")
      persist(RequestEvent(search)) { event =>
        updateState(event)
        context.system.eventStream.publish(event)
      }

      val query = Query(search.term)
      SearchEngineGate.refs.foreach(r => r ! query)

    case result: Result =>
      log.info(s"persistent actor: receive result: ${result.result}")
      persist(ResultEvent(result)) { event =>
        updateState(event)
        db.update(state.search) //todo: persist with journal plugin
        context.system.eventStream.publish(event)
      }
  }
}
