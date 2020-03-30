package parallelprogramming.week1.parallelize

import parallelprogramming._
/**
  * Parallelization of a simple task to calculate the p-norm of a vector.
  *
  * {{@see https://en.wikipedia.org/wiki/Norm_(mathematics)#p-norm}}
  *
  * In my machine the first approach is faster than the second one
  */
object PNormParallelization extends App {

  val arr = (1 to 20000).toArray

  def sumSegment(arr: Array[Int], p: Double, s: Int, t: Int): Double =
    (s until t).foldLeft(0D)((acum, pos) => acum + Math.round(Math.pow(arr(pos), p)))

  // Unparallel p-norm.
  def pNorm(arr: Array[Int], p: Double): Double =
    Math.pow(sumSegment(arr, p, 0, arr.length), 1/p)

  // Splitted parallel
  def pNormSplit(arr: Array[Int], p: Double): Double = {
    val m = arr.length / 2
    val (sum1, sum2) = parallel(
      sumSegment(arr, p, 0, m),
      sumSegment(arr, p, m, arr.length)
    )

    Math.pow(sum1 + sum2, 1/p)
  }

  println("Sequential block:")
  time(pNorm(arr, 14))
  println(measureWarmup(pNorm(arr, 14)))

  Thread.sleep(500)

  println("Parallel block")
  time(pNormSplit(arr, 14))
  println(measureWarmup(pNormSplit(arr, 14)))
}
