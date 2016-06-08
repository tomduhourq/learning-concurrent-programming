package learningconcurrency.ch2

import learningconcurrency._
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
  def pNorm(arr: Array[Int], p: Double): Double = {
    var pNorm = 0D
    val completeSum = thread{pNorm = Math.pow(sumSegment(arr, p, 0, arr.length), 1/p)}
    completeSum.join()
    pNorm
  }

  // Splitted parallel
  def pNormSplit(arr: Array[Int], p: Double): Double = {
    var partialSum1 = 0D
    var partialSum2 = 0D
    val m = arr.length / 2
    val (sum1, sum2) = (
      thread {
        partialSum1 += sumSegment(arr, p, 0, m)
        log(s"Finished processing sum 1 with value: $partialSum1")
      },
      thread {
        partialSum2 += sumSegment(arr, p, m, arr.length)
        log(s"Finished processing sum 2 with value: $partialSum2")
      })

    sum1.join()
    sum2.join()

    Math.pow(partialSum1 + partialSum2, 1/p)
  }

  println(s"Unparallel 14-Norm of vector (1 to 20000): ${time(pNorm(arr, 14))}")

  Thread.sleep(500)

  println(s"Parallel 14-Norm of vector (1 to 20000): ${time(pNormSplit(arr, p = 14))}")
}