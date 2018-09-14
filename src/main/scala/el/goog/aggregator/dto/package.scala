package el.goog.aggregator.dto

import spray.json.DefaultJsonProtocol._
import spray.json.RootJsonFormat


case class Task(term: String, priority: Int, num: Int)

case class Response(id: Int)

case class Result(id: Int, results: Seq[String])


class Dto {
  implicit val responseFormat: RootJsonFormat[Response] = jsonFormat1(Response)
  implicit val taskFormat: RootJsonFormat[Task] = jsonFormat3(Task)
  implicit val resultFormat: RootJsonFormat[Result] = jsonFormat2(Result)

}
