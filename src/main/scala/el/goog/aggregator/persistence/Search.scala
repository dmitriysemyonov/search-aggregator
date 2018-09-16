package el.goog.aggregator.persistence

import org.joda.time.DateTime


/**
  * Entity.
  */
//CREATE TABLE goog.search ( id bigint primary key, term text, priority int, num bigint, lastmodified timestamp result set<text> );
case class Search(
                   id: Int = -1,
                   term: String,
                   priority: Int,
                   num: Int,
                   lastModified: DateTime = DateTime.now(),
                   result: Set[String] = Set.empty
                 )
