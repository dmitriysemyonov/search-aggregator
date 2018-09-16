package el.goog.aggregator.util

import com.typesafe.scalalogging.Logger

trait Log { self =>

  val log = Logger(self.getClass)

}
