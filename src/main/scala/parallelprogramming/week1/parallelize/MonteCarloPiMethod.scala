package parallelprogramming.week1.parallelize

import parallelprogramming._

import scala.util.Random

/**
  * Monte carlo pi method to estimate Pi for a circle of radius 1.
  */
object MonteCarloPiMethod extends App {

  val RADIUS = 1

  /**
    * Pick points randomly inside the circle and retrieve the count.
    * This code is obviously sequential.
    *
    * @param n how many iterations should we make.
    * @return the count.
    */
  def monteCarloCount(n: Int): Int = {
    @annotation.tailrec
    def countRec(left: Int, hits: Int): Int = {
      if(left == 0) hits
      else {
        val x = Random.nextDouble
        val y = Random.nextDouble
        if(x*x + y*y < RADIUS)
          countRec(left - 1, hits + 1)
        else
          countRec(left - 1, hits)
      }
    }
    countRec(n, 0)
  }


  def monteCarloPi(iterations: Int): Double = (4 * monteCarloCount(iterations)) / iterations.toDouble

  println("Sequential Measure:")
  time(monteCarloPi(220))
  println(measureWarmup(monteCarloPi(220)))

  /**
    * Parallelization in 4 cores of monte carlo technique.
    *
    * @param iterations number of random points we want to take.
    * @return
    */
  def parallelMonteCarloPi(iterations: Int): Double = {
    def quarter = monteCarloCount(iterations/4)
    val ((s1, s2), (s3, s4)) = parallel(
      parallel(quarter, quarter),
      parallel(quarter, quarter)
    )
    (4 * (s1 + s2 + s3 + s4)) / iterations.toDouble
  }

  println("Parallel measure:")
  time(parallelMonteCarloPi(220))
  println(measureWarmup(parallelMonteCarloPi(220)))
}
