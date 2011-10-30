package shop

import org.scalatest.junit.JUnitRunner
import org.scalatest.FeatureSpec
import org.scalatest.GivenWhenThen
import org.scalatest.matchers.MustMatchers
import org.junit.runner.RunWith
import org.joda.time.DateTime

@RunWith(classOf[JUnitRunner])
class TodoSpec extends FeatureSpec with GivenWhenThen with MustMatchers {
  Category.apply
  feature("Tests that fail for as yet unknown reasons") {
    info("Dynamic collection of tests that represent work in progress")

  }
}
