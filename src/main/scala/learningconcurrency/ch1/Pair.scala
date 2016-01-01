package learningconcurrency.ch1

/**
  * Created by tom on 1/1/16.
  */
class Pair[P, Q](val first: P, val second: Q) {
  def unapply = Ch1.fuse(Option(first), Option(second))
}
