package learningconcurrency.ch3

import java.util.concurrent.TimeUnit

import learningconcurrency._

import scala.concurrent.ExecutionContext
import scala.concurrent.forkjoin.ForkJoinPool

/**
 * Thread pools: facilities that maintain a certain number of threads
 * in a waiting state and start running when a task is submitted.
 *
 * Executor is a Java interface that helps us decouple the logic of
 * concurrent tasks from how they are indeed executed.
 */
object Executors_1 extends App {

  /**
   * We start with a simple Runnable that wants to be asynchronously run
   */
  val runnable = new Runnable {
    override def run(): Unit = log("Running task asynchronously!")
  }

  /**
   * Submit a task to be asynchronously executed in a Java ForkJoinPool
   */
  val executor = new ForkJoinPool
  executor.execute(runnable)

  /**
   * Always make sure we call some kind of termination on executors.
   * Executors mainly possess a shutdown() method, which
   * gracefully terminates the executor by executing all submitted
   * tasks and stopping all the worker threads, adding an awaitTermination()
   */
  executor.shutdown()
  executor.awaitTermination(60, TimeUnit.SECONDS)

  /**
   * Scala uses its own Executor implementation, the ExecutionContext
   */
  val ectx = ExecutionContext.global
  ectx.execute(runnable)

  /**
   * We can also tune the execution context by creating one with
   * an already existing Executor Service, or Executor.
   *
   * In the example, we create a pool with parallelism level 2
   * This means it has allocated 2 waiting threads to be used.
   */
  val ectxForkJoin = ExecutionContext.fromExecutorService(new ForkJoinPool(2))
  ectxForkJoin.execute(runnable)

  /**
   * This abstractions seem quite happy, but they come with caveats.
   * If the pool runs out of threads, they are unable to execute
   * following tasks until they free some of the active threads.
   *
   * The global Execution Context mimics the architecture it is
   * running on, so on my personal quad core machine, I have 8 threads
   * at most, because of hyper threading, to submit to it.
   *
   * When I run this, tasks terminate in batches of 8 in an asynchronous
   * fashion, not 32 at the same time.
   */
  for(i <- 0 until 32) execute {
    Thread.sleep(2000)
    log(s"Task $i completed.")
  }
  Thread.sleep(10000)
}
