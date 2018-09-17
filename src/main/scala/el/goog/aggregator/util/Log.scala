package el.goog.aggregator.util

import com.typesafe.scalalogging.Logger

/**
  * Simple logger wrapper for convenience.
  */
trait Log { self =>

  val log = Logger(self.getClass)

}
