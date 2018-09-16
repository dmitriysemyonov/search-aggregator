package el.goog.aggregator.search

trait SearchEngine {
  def name: String

  def find(id: Int, num: Int, term: String) : List[String]

}
