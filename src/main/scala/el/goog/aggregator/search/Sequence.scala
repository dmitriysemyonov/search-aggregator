package el.goog.aggregator.search

/**
  * Sequence generator for search tasks.
  */
object Sequence {
  private var sequence: Int = 0

  def next(): Int = {
    sequence += 1
    sequence
  }
}
