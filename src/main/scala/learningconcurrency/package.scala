import java.text.DecimalFormat
import java.util.concurrent.{ForkJoinPool, ForkJoinTask, RecursiveTask}

import scala.util.DynamicVariable
import org.scalameter._

package object learningconcurrency {

  def log(msg: String) = println(s"${Thread.currentThread.getName}: $msg")

  /**
    * Creates and starts a thread with the block passed by parameter
    * so clients can call the join() method freely.
    *
    * @param body the code block to execute.
    * @return the started thread.
    */
  def thread(body: =>Unit) = {
    val t = new Thread {
      override def run() = body
    }
    t.start()
    t
  }

  /**
    * Evaluates and times the given block of code in a naive way.
    *
    * - Does not make repetitions
    * - Does not take the variance nor the mean of running the computation
    * - Does not eliminate outliers.
    * - Does not 'warm-up' before executing the computation.
    *
    * @param block the computation to time and return.
    * @tparam A the type of the computation.
    * @return the result value of the computation.
    */
  def time[A](block: => A) = {
    val initialTime = System.nanoTime
    val evaluation = block
    println(s"Resolved computation in ${numberFormat.format((System.nanoTime - initialTime)/1e6)}ms.")
    println(evaluation)
  }

  /**
    * Make a warm-up before measuring the computation.
    *
    * @param block the block to execute.
    * @tparam A the return type of the block.
    * @return the time it took for the computation to execute with warm-up.
    */
  def measureWarmup[A](block: =>A) = withWarmer(new Warmer.Default) measure block

  private val numberFormat = new DecimalFormat("#####.############")

  /****************************************
    ****** Utils for parallelism **********
    ***************************************/

  val forkJoinPool = new ForkJoinPool

  abstract class TaskScheduler {
    def schedule[T](body: => T): ForkJoinTask[T]
    def parallel[A, B](taskA: => A, taskB: => B): (A, B) = {
      val right = task { taskB }
      val left = taskA
      (left, right.join())
    }
  }

  class DefaultTaskScheduler extends TaskScheduler {
    def schedule[T](body: => T): ForkJoinTask[T] = {
      val t = new RecursiveTask[T] {
        def compute = body
      }
      forkJoinPool.execute(t)
      t
    }
  }

  val scheduler =
    new DynamicVariable[TaskScheduler](new DefaultTaskScheduler)

  def task[T](body: => T): ForkJoinTask[T] = {
    scheduler.value.schedule(body)
  }

  def parallel[A, B](taskA: => A, taskB: => B): (A, B) = {
    scheduler.value.parallel(taskA, taskB)
  }

  /**
    * Join a Task implicitly to extract its value.
    *
    * @param task the task to await
    * @tparam A the return type of the task
    * @return the value from the provided task.
    */
  implicit def forkJoinTaskValue[A](task: ForkJoinTask[A]): A = task.join()

  /**
    * Parallelizes two tasks and return their value, one in the main thread,
    * and the other in parallel to the first one.
    *
    * Time to await: max(taskA, taskB)
    *
    * @param taskA the task to execute in the main thread
    * @param taskB the task to execute in another thread
    * @tparam A the return type of taskA
    * @tparam B the return type of taskB
    * @return the result of both computations
    */
  def parallelInTermsOfTask[A, B](taskA: => A, taskB: => B): (A, B) = (taskA, task{taskB})
}
