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
}
