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
    scenario("A menu may contain more than one recipe per day") {
      given("a menu with dish and dish2 on Saturday and a cookbook with these recipes")
      val menuAsString = """Zaterdag valt op:08102011
      	zaterdag:dish1
      	zaterdag:dish2
      """
      val cookBook = """naam:dish1
groente:witlof

naam:dish2
groente:salad
"""
      val menu = Menu(menuAsString, CookBook(cookBook))
      when("a shoppinglist is generated")
      val shoppingList = new ShoppingList(menu)
      then("the list contains ingredients for both recipes")
      val expectedShoppingList = """zaterdag:dish1
zondag:dish2

groente:salad(08-10)
witlof(08-10)"""
     // expectedShoppingList must be === shoppingList.printShoppinglistForUseWhileShopping
      pending
    }
  }

}
