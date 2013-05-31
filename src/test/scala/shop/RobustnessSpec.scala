package shop

import org.scalatest.junit.JUnitRunner
import org.scalatest.FeatureSpec
import org.scalatest.GivenWhenThen
import org.scalatest.matchers.MustMatchers
import org.junit.runner.RunWith

@RunWith(classOf[JUnitRunner])
class RobustnessSpec extends FeatureSpec with GivenWhenThen with MustMatchers {
  Ingredient.categoryClient = new CategoryClient(LargeCategoryTestConfig)

  feature("Shoppinglist can parse a list of groceries per recipe from a text file") {
    info("As a family member")
    info("I want ShoppingList to be resilient to errors in cookbook or menu specifications")
    info("So that I can find out easily what went wrong")

    scenario("A exception is thrown When an unknown recipe is added to a menu") {
      Given("a Menu and a CookBook")
      val menuAsString = """Zaterdag valt op:08102011
      	zaterdag:Witlof met kip
      	zondag:Nasi
        dinsdag:Non existent recipe
      """
      When("the shoppinglist is created")
      Then("but a exception is thrown")
      intercept[PanicException] {
        val extractedLocalValue = new CookBookClient(TestCookBookConfig)
        val menu = Menu(menuAsString, extractedLocalValue)
        val shoppingList = new ShoppingList(menu)
        shoppingList.printShoppinglist
      }
    }

    scenario("Avoid crash if recipe contains no more than a title") {
      Given("a recipe may consist of only a title")
      val cookBookAsText = """naam:dish1
 
"""
      When("a cookbook is read with a single recipe that is no more than a title")
      val cookbook = CookBook.loadFromText(cookBookAsText)
      Then("the recipe is parsed without errors")
      val expectedCookbook = Map[String, Recipe]("dish1" -> new Recipe("dish1", List()))
      expectedCookbook must be === cookbook
    }

    scenario("A menu may contain - in stead of a recipe to indicate no dish is needed on a particular day") {
      Given("a menu with - in stead of a recipe")
      val menuAsString = """Zaterdag valt op:08102011
      	zaterdag:-
        zondag:dish1
      """
      val menu = Menu(menuAsString, new CookBookClient(TestCookBookConfig))
      When("a shoppinglist is generated...")
      val shoppingList = new ShoppingList(menu)
      Then("... that contains only witlof")
      val expectedShoppingList = """zondag:dish1

groente:witlof(09-10)"""
      expectedShoppingList must be === shoppingList.printShoppinglistForUseWhileShopping
    }

    scenario("A cookbook may contain recipes with no more than a title") {
      Given("a cookbook with a dish without ingredients")
      val cookBookAsString = """naam:dish1
        """
      When("the cook book is parsed")
      val cookBook = CookBook.loadFromText(cookBookAsString)
      Then("it contains one recipe with an empty list of ingredients")
      //TODO: Refactor
      val dish1 = cookBook.get("dish1")
      val z = dish1.toList.head
      "dish1" must be === z.name
      List() must be === z.ingredients
    }

    scenario("A day in a menu may be specified in arbitrary case") {
      Given("a menu with days in mixed case")
      val menuAsString = """Zaterdag valt op:08102011
      	zaterdag:dish1
      	Zondag:dish1
        mAAndag:dish1
      """
      val cookBook = """naam:dish1
groente:witlof
"""
      val menu = Menu(menuAsString, new CookBookClient(TestCookBookConfig))
      When("a shoppinglist is generated")
      val shoppingList = new ShoppingList(menu)
      Then("the list contains lots of witlof")
      val expectedShoppingList = """zaterdag:dish1
zondag:dish1
maandag:dish1

groente:witlof(08-10)
      witlof(09-10)
      witlof(10-10)"""
      expectedShoppingList must be === shoppingList.printShoppinglistForUseWhileShopping
    }

    scenario("The app should not crash if a menu line contains no more than the name of the day") {
      Given("a menu with one line that contains only the name of the day")
      val menuAsString = """Zaterdag valt op:08102011
      	zaterdag
        zondag:dish1
      """
      val cookBook = """naam:dish1
groente:witlof
"""
      val menu = Menu(menuAsString, new CookBookClient(TestCookBookConfig))
      When("a shoppinglist is generated")
      val shoppingList = new ShoppingList(menu)
      Then("the list contains witlof, the data for Saturday are ignored")
      val expectedShoppingList = """zondag:dish1

groente:witlof(09-10)"""
      expectedShoppingList must be === shoppingList.printShoppinglistForUseWhileShopping
    }
  }
}
