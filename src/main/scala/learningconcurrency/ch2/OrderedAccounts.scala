package learningconcurrency.ch2

import learningconcurrency._

object OrderedAccounts extends App {

  var id = 0L
  def getUniqueId(): Long = this.synchronized {
    val toReturn = id
    id = id + 1
    toReturn
  }

  class Account(val name: String, var money: Int) {
    val uid = getUniqueId()
  }

  def send(a1: Account, a2: Account, n: Int): Unit = {
    def adjust() = {
      a1.money -= n
      a2.money += n
    }
    if(a1.uid < a2.uid)
      a1.synchronized { a2.synchronized { adjust() }}
    else
      a2.synchronized{ a1.synchronized{ adjust() }}
  }

  val a = new Account(" Jack", 1000)
  val b = new Account(" Jill", 2000)
  val t1 = thread { for (i <- 0 until 100) send( a, b, 1) }
  val t2 = thread { for (i <- 0 until 100) send( b, a, 1) }
  t1. join(); t2. join()
  log( s" a = ${ a.money}, b = ${ b.money}")

}
