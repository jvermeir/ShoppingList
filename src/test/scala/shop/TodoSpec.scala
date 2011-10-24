package shop

import org.scalatest.junit.JUnitRunner
import org.scalatest.FeatureSpec
import org.scalatest.GivenWhenThen
import org.scalatest.matchers.MustMatchers
import org.junit.runner.RunWith
import org.joda.time.DateTime

@RunWith(classOf[JUnitRunner])
class TodoSpec extends FeatureSpec with GivenWhenThen with MustMatchers {
  feature("Tests that fail for as yet unknown reasons") {
    info("Dynamic collection of tests that represent work in progress")

    scenario("A list of extra's (not related to a recipe) may be added to a menu") {
      given("a menu with one recipe and a list of extra's")
      val menuAsString = """Zaterdag valt op:08102011
      	zaterdag:dish1

        extra
      	meel:meel
      	zeep:vaatwasmiddel
      """
      val cookBook = """naam:dish1
groente:witlof
"""
      when("a shoppinglist is generated")
      val menuAndList = ShoppingList.split(menuAsString)
      val menu = Menu(menuAndList._1, CookBook(cookBook))
      val extras: List[Ingredient] = Ingredient.readFromText(menuAndList._2)
      val shoppingList = new ShoppingList(menu, extras)
      then("the list contains witlof and the two extra's")
      val expectedShoppingList = """zaterdag:dish1

groente:witlof(08-10)
meel:meel
zeep:vaatwasmiddel"""
      expectedShoppingList must be === shoppingList.printShoppinglistForUseWhileShopping
    }
  }
}
