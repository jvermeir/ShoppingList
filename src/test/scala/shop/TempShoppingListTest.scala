package shop

import org.joda.time.DateTime
import org.scalatest.{FeatureSpec, GivenWhenThen, MustMatchers}

class TempShoppingListTest extends FeatureSpec with GivenWhenThen with MustMatchers {

  implicit object FileCategoryConfig extends Config {
    lazy val categoryStore = new FileBasedCategoryStore("data/test/categoryDatabase.csv")
    lazy val cookBookStore = new CookbookStoreForShoppingListTest
  }

  val cookbook = new CookBook

  feature("Shoppinglist can parse a list of groceries per recipe from a text file") {
    scenario("The day of the month is added at the start of each menu line on the shopping list ") {
      Given("a menu with two recipes for sat october 8th 2011")
      val menuAsString =
        """Zaterdag valt op:08102011
           zaterdag:dish1
           zondag:dish2
        """
      When("a shoppinglist is generated")
      val menuAndList = ShoppingList.split(menuAsString)
      val menu = Menu(menuAndList._1, cookbook)
      val extras: List[Ingredient] = Ingredient.readFromText(menuAndList._2)
      val shoppingList = new ShoppingList(menu, extras)
      Then("Saturday is printed as '8 Zaterdag' and Sunday is printed as '9 Zondag'")
      val expectedShoppingList = """8 zaterdag:dish1
9 zondag:dish2

groente:
      appels(09-10)
      witlof(08-10)

recepten:
zaterdag:name:dish1
groente:witlof

zondag:name:dish2
groente:appels
""".stripMargin

      expectedShoppingList mustBe shoppingList.printShoppinglistForUseWhileShopping.stripMargin
    }
  }
}

