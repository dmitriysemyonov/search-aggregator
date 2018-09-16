package el.goog.aggregator.search

import akka.actor.Actor
import el.goog.aggregator.util.Log


case class Query(query: String)

case class Result(result: List[String])


class DummySearchEngine extends Actor with Log {
  def name: String = "dummy"

  val num = 2
  val offset = 0

  def find(term: String, num: Int): List[String] = {
    for (i <- List.range(0 + offset, num + offset)) yield s"http://$name/$term/$i"
  }


  override def receive: Receive = {
    case evt: Query =>
      log.debug(s"got query: ${evt.query}")

      val result = find(evt.query, num)

      log.debug(s"result ready: $result")
      sender() ! Result(result)
  }
}
