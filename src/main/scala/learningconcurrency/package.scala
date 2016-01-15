package object learningconcurrency {
  def log(msg: String) = println(s"${Thread.currentThread.getName}: $msg")
  def thread(body: =>Unit) = {
    val t = new Thread {
      override def run() = body
    }
    t.start()
    t
  }
}
