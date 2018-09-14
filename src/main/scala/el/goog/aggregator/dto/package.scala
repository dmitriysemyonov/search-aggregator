package el.goog.aggregator.dto

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.{DefaultJsonProtocol, RootJsonFormat}

/**
  * Request for submitting new search task.
  *
  * @param term     search term
  * @param priority priority, lower number correspond to higer priority
  * @param num      number results to show
  */
case class Task(term: String, priority: Int, num: Int)

/**
  * Task submission response.
  *
  * @param id search request identifier
  */
case class Response(id: Int)

/**
  * Search results.
  *
  * @param id      search request identifier
  * @param results list of result URLs
  */
case class Result(id: Int, results: Seq[String])



/**
  * Support objects for implicit conversion to JSON
  */

object TaskJsonSupport extends DefaultJsonProtocol with SprayJsonSupport {
  implicit val taskFormat: RootJsonFormat[Task] = jsonFormat3(Task)
}

object ResponseJsonSupport extends DefaultJsonProtocol with SprayJsonSupport {
  implicit val responseFormat: RootJsonFormat[Response] = jsonFormat1(Response)
}

object ResultJsonSupport extends DefaultJsonProtocol with SprayJsonSupport {
  implicit val resultFormat: RootJsonFormat[Result] = jsonFormat2(Result)
}
