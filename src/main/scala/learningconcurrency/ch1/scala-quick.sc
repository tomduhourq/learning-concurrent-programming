//simple function
val twice: Int => Int = _ * 2
val negate: Int => Int = -_

//method to run twice a block
def runTwice(block: =>Unit) = {
  block
  block
}

runTwice { println("Hi") }
//for-comprehension quick
for (i <- 0 until 10) println(i)

//traduction by Scala compiler
(0 until 10) foreach println
//transformation (if we put only the object it would yield a Seq[Int => Int])
for(n <- 0 until 10) yield negate.apply(n)
//traduction by Scala compiler
(0 until 10) map negate
//cross join
for(x <- 0 until 4 ; y <- 0 until 4) yield (x, y)
// join in parallel
(0 until 4) zip (0 until 4)

// Exercise 4
new Pair(2, 3) match {
  case Pair(x, y) => s"Correctly extracted elements $x and $y."
  case _ => s"Define the unapply method correctly."
}