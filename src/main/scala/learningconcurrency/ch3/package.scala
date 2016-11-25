package learningconcurrency

import scala.concurrent.ExecutionContext

package object ch3 {

  /**
   * Gets hold of the global ExecutionContext to submit
   * a task that wants to be run asynchronously
   *
   * @param body the body to execute
   */
  def execute(body: =>Unit): Unit = ExecutionContext.global.execute(new Runnable { override def run(): Unit = body })
}
