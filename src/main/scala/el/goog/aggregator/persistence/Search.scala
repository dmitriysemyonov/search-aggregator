package el.goog.aggregator.persistence

import org.joda.time.DateTime


/**
  * Entity.
  */
case class Search(
                   id: Int = -1,
                   term: String,
                   priority: Int,
                   num: Int,
                   lastModified: DateTime = DateTime.now(),
                   result: Set[String] = Set.empty
                 )
