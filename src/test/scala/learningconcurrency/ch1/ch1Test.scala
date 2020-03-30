package learningconcurrency.ch1

import scala.util.Try
import org.scalatest._
import org.scalatest.matchers.should.Matchers

/**
  * Created by tom on 1/1/16.
  */
class ch1Test extends FreeSpec with Matchers {

  "Exercises from Ch1:" - {
    "1. Compose" in {
      val twice: Int => Int = _ * 2
      val toDouble: String => Double = (x: String) => (x + ".1").toDouble
      val stringify: Int => String = _.toString
      Ch1.compose(stringify, twice).apply(2) should be ("4")
      Ch1.compose(toDouble, stringify).apply(56) should be (56.1)
    }

    "2. Fuse" in {
      val opt1 = Try(Option(31/0)) recover {case _: ArithmeticException => None} getOrElse None
      val opt2 = Some(23)
      val opt3 = Some(10)
      Ch1.fuse(opt1, opt2) shouldBe empty
      Ch1.fuse(opt2, opt3) should be (Some((23, 10)))
      Ch1.fuse2(opt3, opt2) should be (Some(10, 23))
    }

    "3. Check" in {
      val even: Int => Boolean = _ % 2 == 0
      Ch1.check(0 until 10 by 2)(even) should be (true)
      Ch1.check2(0 until 14 by 3)(even) should be (false)
    }

    "4. Permutations" in {
      Ch1.permutations("ok").toList should be (List("ok", "ko"))
      Ch1.permutations("try").length should be (6)
    }
  }
}
