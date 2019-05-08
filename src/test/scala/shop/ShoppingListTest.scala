package shop

import org.joda.time.DateTime
import org.scalatest.{FeatureSpec, GivenWhenThen, MustMatchers}

class ShoppingListTest extends FeatureSpec with GivenWhenThen with MustMatchers {

  implicit object FileCategoryConfig extends Config {
    lazy val categoryStore = new FileBasedCategoryStore("data/test/categoryDatabase.csv")
    lazy val cookBookStore = new CookbookStoreForShoppingListTest
  }

  val cookbook = new CookBook

  feature("Shoppinglist can parse a list of groceries per recipe from a text file") {
    info("As a family member")
    info("I want to list recipes as text in a file")
    info("So that I can use 'm to create a week-menu")

    scenario("A recipe for witlof results in a list of groceries sorted by category") {
      Given("A list of ingredients as strings for the witlof recipe")
      val witlofIngredientsAsText = """naam:Witlof met kip
      				vlees:kipfilet plakjes
      				pasta:gezeefde tomaten
      				rijst:rijst
      				diepvries:
      				groente:witlof
      				zuivel:geraspte kaas"""
      When("parse is invoked")
      val witlofRecipe = Recipe(witlofIngredientsAsText)
      Then("a list of groceries ordered by category is returned and the recipe's name is 'Witlof met kip'")
      val listOfExpectedIngredients = List(
        new Ingredient("zuivel", "geraspte kaas"),
        new Ingredient("vlees", "kipfilet plakjes"),
        new Ingredient("groente", "witlof"),
        new Ingredient("pasta", "gezeefde tomaten"),
        new Ingredient("rijst", "rijst"))
      val expectedWitlofRecipe = new Recipe("Witlof met kip", listOfExpectedIngredients)
      expectedWitlofRecipe mustBe witlofRecipe
    }

    scenario("A menu with witlof on Saturday and Nasi on Sunday results in a list of ingredients") {
      Given("a menu with witlof on Saturday and Nasi on Sunday and a kookbook with these recipes")
      val menuAsString =
        """Zaterdag valt op:08102011
      	zaterdag:Witlof met kip
      	zondag:Nasi
        """
      When("a menu is generated")
      val menu = Menu(menuAsString, cookbook)
      val extras: List[Ingredient] = List()
      val shoppingList = new ShoppingList(menu, extras)
      Then("the list of ingredients on the shopping list equals the list of ingredients from both categories combined ordered by category name")
      val expectedListOfIngredients = List(
        ShoppingListItem(new Ingredient("zuivel", "ei"), new DateTime(2011, 10, 9, 0, 0)),
        ShoppingListItem(new Ingredient("zuivel", "geraspte kaas"), new DateTime(2011, 10, 8, 0, 0)),
        ShoppingListItem(new Ingredient("zuivel", "vloeibare bakboter"), new DateTime(2011, 10, 9, 0, 0)),
        ShoppingListItem(new Ingredient("vlees", "kipfilet"), new DateTime(2011, 10, 9, 0, 0)),
        ShoppingListItem(new Ingredient("vlees", "kipfilet plakjes"), new DateTime(2011, 10, 8, 0, 0)),
        ShoppingListItem(new Ingredient("groente", "nasi pakket"), new DateTime(2011, 10, 9, 0, 0)),
        ShoppingListItem(new Ingredient("groente", "witlof"), new DateTime(2011, 10, 8, 0, 0)),
        ShoppingListItem(new Ingredient("sauzen", "sate saus"), new DateTime(2011, 10, 9, 0, 0)),
        ShoppingListItem(new Ingredient("pasta", "gezeefde tomaten"), new DateTime(2011, 10, 8, 0, 0)),
        ShoppingListItem(new Ingredient("rijst", "kroepoek"), new DateTime(2011, 10, 9, 0, 0)),
        ShoppingListItem(new Ingredient("rijst", "rijst"), new DateTime(2011, 10, 8, 0, 0)),
        ShoppingListItem(new Ingredient("rijst", "rijst"), new DateTime(2011, 10, 9, 0, 0)),
        ShoppingListItem(new Ingredient("olie", "augurken"), new DateTime(2011, 10, 9, 0, 0)),
        ShoppingListItem(new Ingredient("olie", "zilveruitjes"), new DateTime(2011, 10, 9, 0, 0)))
      val expectedIngredientsSortedByCategory = expectedListOfIngredients.sortWith(_ < _)
      expectedListOfIngredients mustBe shoppingList.shoppingListItemsSortedByCategory
    }

    scenario("A menu with witlof on Sunday and Nasi on Monday results in a list of groceries") {
      Given("a menu with witlof on Sunday and Nasi on Monday and a kookbook with these recipes")
      val menuAsString =
        """Zaterdag valt op:08102011
      	zondag:Witlof met kip
      	maandag:Nasi
        """
      When("a menu is generated")
      val menu = Menu(menuAsString, cookbook)
      val extras: List[Ingredient] = List()
      val shoppingList = new ShoppingList(menu, extras)
      Then("a shopping list is produced")
      val expectedShoppingList = """ei
geraspte kaas
vloeibare bakboter
kipfilet
kipfilet plakjes
nasi pakket(10-10)
witlof(09-10)
sate saus
gezeefde tomaten
kroepoek
rijst
rijst
augurken
zilveruitjes"""
      expectedShoppingList mustBe shoppingList.printShoppinglist
    }

    scenario("A menu and list of groceries are printed for use while shopping") {
      Given("a menu with witlof on Saturday and Nasi on Sunday and a kookbook with these recipes")
      val menuAsString =
        """Zaterdag valt op:08102011
      	zaterdag:Witlof met kip
      	zondag:Nasi
        """
      When("a menu is generated")
      val menu = Menu(menuAsString, cookbook)
      val extras: List[Ingredient] = List()
      val shoppingList = new ShoppingList(menu, extras)
      Then("and a shopping list is printed")
      val expectedShoppingList = """8 zaterdag:Witlof met kip
9 zondag:Nasi

zuivel:
      ei
      geraspte kaas
      vloeibare bakboter
vlees:
      kipfilet
      kipfilet plakjes
groente:
      nasi pakket(09-10)
      witlof(08-10)
sauzen:
      sate saus
pasta:
      gezeefde tomaten
rijst:
      kroepoek
      rijst
      rijst
olie:
      augurken
      zilveruitjes

recepten:
zaterdag:name:Witlof met kip
zuivel:geraspte kaas
vlees:kipfilet plakjes
groente:witlof
pasta:gezeefde tomaten
rijst:rijst

zondag:name:Nasi
zuivel:ei
zuivel:vloeibare bakboter
vlees:kipfilet
groente:nasi pakket
sauzen:sate saus
rijst:kroepoek
rijst:rijst
olie:augurken
olie:zilveruitjes
"""

      expectedShoppingList mustBe shoppingList.printShoppinglistForUseWhileShopping
    }

    scenario("A menu may contain more than one recipe per day") {
      Given("a menu with dish and dish2 on Saturday and a cookbook with these recipes")
      val menuAsString =
        """Zaterdag valt op:08102011
      	zaterdag:Witlof met kip
      	zaterdag:Nasi
        """
      val menu = Menu(menuAsString, cookbook)
      When("a shoppinglist is generated")
      val extras: List[Ingredient] = List()
      val shoppingList = new ShoppingList(menu, extras)
      Then("the list contains ingredients for both recipes")
      val expectedShoppingList = """8 zaterdag:Witlof met kip
8 zaterdag:Nasi

zuivel:
      ei
      geraspte kaas
      vloeibare bakboter
vlees:
      kipfilet
      kipfilet plakjes
groente:
      nasi pakket(08-10)
      witlof(08-10)
sauzen:
      sate saus
pasta:
      gezeefde tomaten
rijst:
      kroepoek
      rijst
      rijst
olie:
      augurken
      zilveruitjes

recepten:
zaterdag:name:Witlof met kip
zuivel:geraspte kaas
vlees:kipfilet plakjes
groente:witlof
pasta:gezeefde tomaten
rijst:rijst

zaterdag:name:Nasi
zuivel:ei
zuivel:vloeibare bakboter
vlees:kipfilet
groente:nasi pakket
sauzen:sate saus
rijst:kroepoek
rijst:rijst
olie:augurken
olie:zilveruitjes
"""
      expectedShoppingList mustBe shoppingList.printShoppinglistForUseWhileShopping
    }

    scenario("A list of extra's (not related to a recipe) may be added to a menu") {
      Given("a menu with one recipe and a list of extra's")
      val menuAsString =
        """Zaterdag valt op:08102011
      	zaterdag:dish1

        extra
      	meel:meel
      	zeep:vaatwasmiddel
        """
      When("a shoppinglist is generated")
      val menuAndList = ShoppingList.split(menuAsString)
      val menu = Menu(menuAndList._1, cookbook)
      val extras: List[Ingredient] = Ingredient.readFromText(menuAndList._2)
      val shoppingList = new ShoppingList(menu, extras)
      Then("the list contains witlof and the two extra's")
      val expectedShoppingList = """8 zaterdag:dish1

groente:
      witlof(08-10)
meel:
      meel
zeep:
      vaatwasmiddel

recepten:
zaterdag:name:dish1
groente:witlof
"""
      expectedShoppingList mustBe shoppingList.printShoppinglistForUseWhileShopping
    }

    scenario("A list of recipes for the week is added to the shopping list") {
      Given("a menu with two recipes")
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
      Then("the list contains two recipes")
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

class CookbookStoreForShoppingListTest(implicit val config: Config) extends CookBookStore {
  override def reload: Unit = {
    loadFromText(
      """naam:Witlof met kip
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

      naam:dish1
      groente:witlof

      naam:dish2
      groente:appels
      """.stripMargin)
  }

  override
  def save: Unit = {}
}