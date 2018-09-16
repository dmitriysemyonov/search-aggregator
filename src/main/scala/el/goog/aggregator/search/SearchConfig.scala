package el.goog.aggregator.search

import el.goog.aggregator.util.Conf

import scala.collection.JavaConverters._
import scala.collection.mutable

object SearchConfig {
  private val config = Conf.config

  val providers: mutable.Buffer[String] = config.getStringList("search.provider").asScala
}
