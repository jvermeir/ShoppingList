package shop

import org.scalatest.junit.AssertionsForJUnit
import scala.collection.mutable.ListBuffer
import org.junit.Assert._
import org.junit.Test

class DisplayTest extends AssertionsForJUnit {
  @Test
  def testIfItWorks = {
    assert ("Hello, world" === "Hello, world")
  }
}
