package shop

import org.scalatest.junit.JUnitRunner
import org.scalatest.FeatureSpec
import org.scalatest.GivenWhenThen
import org.scalatest.matchers.MustMatchers
import org.junit.runner.RunWith
import org.joda.time.DateTime

@RunWith(classOf[JUnitRunner])
class RobustnessSpec extends FeatureSpec with GivenWhenThen with MustMatchers {
  feature("Shoppinglist can parse a list of groceries per recipe from a text file") {
    info("As a family member")
    info("I want ShoppingList to be resilient to errors in cookbook or menu specifications")
    info("So that I can find out easily what went wrong")

    scenario("A exception is thrown when an unknown recipe is added to a menu") {
      given("a Menu and a Kookboek")
      val menuAsString = """Zaterdag valt op:08102011
      	zaterdag:Witlof met kip
      	zondag:Nasi
        dinsdag:Non existent recipe
      """
      when("the shoppinglist is created")
      then("but a exception is thrown")
      intercept[NoSuchElementException] {
        val menu = Menu(menuAsString, CookBook(cookBookAsText))
        val shoppingList = new ShoppingList(menu)
        shoppingList.printShoppinglist
      }
    }

    scenario("Avoid crash if recipe contains no more than a title") {
      given("a recipe may consist of only a title")
      val cookBookAsText = """naam:dish1

"""
      when("a cookbook is read with a single recipe that is no more than a title")
      val cookbook = CookBook(cookBookAsText)
      then("the recipe is parsed without errors")
      val expectedCookbook = new CookBook(Map[String, Recipe]("dish1" -> new Recipe("dish1", List())))
      expectedCookbook must be === cookbook
    }

    scenario("A menu may contain - in stead of a recipe to indicate no dish is needed on a particular day") {
      given("a menu with - in stead of a recipe")
      val menuAsString = """Zaterdag valt op:08102011
      	zaterdag:-
        zondag:dish1
      """
      val cookBook = """naam:dish1
groente:witlof
"""
      val menu = Menu(menuAsString, CookBook(cookBook))
      when("a shoppinglist is generated...")
      val shoppingList = new ShoppingList(menu)
      then("... that contains only witlof")
      val expectedShoppingList = """zondag:dish1

groente:witlof(09-10)"""
      expectedShoppingList must be === shoppingList.printShoppinglistForUseWhileShopping
    }

    scenario("A cookbook may contain recipes with no more than a title") {
      given("a cookbook with a dish without ingredients")
      val menuAsString = """Zaterdag valt op:08102011
      	zaterdag:dish1
      """
      val cookBook = """naam:dish1
"""
      val menu = Menu(menuAsString, CookBook(cookBook))
      when("a shoppinglist is generated")
      val shoppingList = new ShoppingList(menu)
      then("the list is empty")
      val expectedShoppingList = """zaterdag:dish1
"""
      expectedShoppingList must be === shoppingList.printShoppinglistForUseWhileShopping
    }

    scenario("A day in a menu may be specified in arbitrary case") {
      given("a menu with days in mixed case")
      val menuAsString = """Zaterdag valt op:08102011
      	zaterdag:dish1
      	Zondag:dish1
        mAAndag:dish1
      """
      val cookBook = """naam:dish1
groente:witlof
"""
      val menu = Menu(menuAsString, CookBook(cookBook))
      when("a shoppinglist is generated")
      val shoppingList = new ShoppingList(menu)
      then("the list contains lots of witlof")
      val expectedShoppingList = """zaterdag:dish1
zondag:dish1
maandag:dish1

groente:witlof(08-10)
      witlof(09-10)
      witlof(10-10)"""
      expectedShoppingList must be === shoppingList.printShoppinglistForUseWhileShopping
    }
  }

  val cookBookAsText = """naam:Witlof met kip
		  vlees:kipfilet plakjes
		  pasta:gezeefde tomaten
		  rijst:rijst
		  diepvries:
		  groente:witlof
		  zuivel:geraspte kaas
		  
		  naam:Nasi
		  groente:nasi pakket
		  vlees:kipfilet
		  sauzen:sate saus
		  rijst:rijst
		  rijst:kroepoek
		  olie:augurken
		  olie:zilveruitjes
		  zuivel:ei
		  zuivel:vloeibare bakboter
		  """
}
