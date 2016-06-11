import java.text.DecimalFormat
import java.util.concurrent.{ForkJoinPool, ForkJoinTask, RecursiveTask}

import scala.util.DynamicVariable

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
    * Evaluates and times the given block of code.
    *
    * @param block the computation to time and return.
    * @tparam A the type of the computation.
    * @return the result value of the computation.
    */
  def time[A](block: => A) = {
    val initialTime = System.currentTimeMillis
    val evaluation = block
    println(s"Resolved block in ${numberFormat.format((System.currentTimeMillis - initialTime)/1e6)}ms.")
    println(evaluation)
  }

  private val numberFormat = new DecimalFormat("#####.############")

  /****************************************
    ****** Utils for parallelism **********
    ***************************************/

  val forkJoinPool = new ForkJoinPool

  abstract class TaskScheduler {
    def schedule[T](body: => T): ForkJoinTask[T]
    def parallel[A, B](taskA: => A, taskB: => B): (A, B) = {
      val right = task {
        taskB
      }
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
}
