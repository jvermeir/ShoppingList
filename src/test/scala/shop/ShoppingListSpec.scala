package shop

import org.scalatest.junit.JUnitRunner
import org.scalatest.FeatureSpec
import org.scalatest.GivenWhenThen
import org.scalatest.matchers.MustMatchers
import org.junit.runner.RunWith
import org.joda.time.DateTime

@RunWith(classOf[JUnitRunner])
class ShoppingListSpec extends FeatureSpec with GivenWhenThen with MustMatchers {

  Ingredient.categoryClient = new CategoryClient(LargeCategoryTestConfig)
  val categoryClient = new CategoryClient(TestCategoryConfig)
  val cookbookClient = new CookBookClient(TestCookBookConfig)

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
      expectedWitlofRecipe must be === witlofRecipe
    }

    scenario("A recipe for witlof and a recipe for Nasi result in a cookbook with two recipes and their groceries") {
      Given("A list of ingredients as strings for the witlof recipe followed by a list of strings for the Nasi recipe")
      When("a new kookboek is created from the text version")
      val cookbook = CookBook.loadFromText(cookBookAsText)
      Then("the cookbook containts a recipe for Witlof and one for Nasi")
      2 must be === cookbook.size
      val witlofRecipe = cookbook.get("Witlof met kip")
      witlofRecipe match {
        case Some(witlofRecipe) => "Witlof met kip" must be === witlofRecipe.name
        case _ => fail ("Witlof recipe not found")
      }
      val nasiRecipe = cookbook.get("Nasi")
      nasiRecipe match {
        case Some(nasiRecipe) => "Nasi" must be === nasiRecipe.name
        case _ => fail ("Nasi recipe not found")
      }
    }
  }

  feature("Shoppinglist can parse a list of groceries per recipe from a text file") {
    info("As a family member")
    info("I want to specify a menu for a week as a list of day:recipe pairs")
    info("So that I can create a shopping list")

    scenario("A menu with witlof on Saturday and Nasi on Sunday results in a list of ingredients") {
      Given("a menu with witlof on Saturday and Nasi on Sunday and a kookbook with these recipes")
      val menuAsString = """Zaterdag valt op:08102011
      	zaterdag:Witlof met kip
      	zondag:Nasi
      """
      When("a menu is generated")
      val menu = Menu(menuAsString, cookbookClient)
      val shoppingList = new ShoppingList(menu)
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
      expectedListOfIngredients must be === shoppingList.shoppingListItemsSortedByCategory
    }

    scenario("A menu with witlof on Sunday and Nasi on Monday results in a list of groceries") {
      Given("a menu with witlof on Sunday and Nasi on Monday and a kookbook with these recipes")
      val menuAsString = """Zaterdag valt op:08102011
      	zondag:Witlof met kip
      	maandag:Nasi
      """
      When("a menu is generated")
      val menu = Menu(menuAsString, cookbookClient)
      val shoppingList = new ShoppingList(menu)
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
      expectedShoppingList must be === shoppingList.printShoppinglist
    }

    scenario("A menu can be read from a file") {
      Given("a menu in a file and a cookbook")
      When("the file is read")
      val cookBook = cookbookClient
      Then("a menu with Witlof met kip and Nasi is created")
      val menu = Menu.readFromFile("data/test/menuForReadFromFileScenario.txt", cookBook)
      2 must be === menu.recipes.size
      menu.recipes(0)._2.name must be === "Witlof met kip"
      menu.recipes(1)._2.name must be === "Nasi"
    }

    scenario("A Cookbook can be read from a file") {
      Given("a cookbook in a text file")
      When("the file is read")
      val cookBook = CookBook.readFromFile("data/test/cookBookForReadFromFileScenario.txt")
      Then("a cook book with a Nasi and a Witlof recipe is created")
      Then("the cook book containts a recipe for Witlof and one for Nasi")
      2 must be === cookBook.size
      "Witlof met kip" must be === cookbookClient.getRecipeByName("Witlof met kip").name
      "Nasi" must be === cookbookClient.getRecipeByName("Nasi").name
    }

    scenario("A menu and list of groceries are printed for use while shopping") {
      Given("a menu with witlof on Saturday and Nasi on Sunday and a kookbook with these recipes")
      val menuAsString = """Zaterdag valt op:08102011
      	zaterdag:Witlof met kip
      	zondag:Nasi
      """
      When("a menu is generated")
      val menu = Menu(menuAsString, cookbookClient)
      val shoppingList = new ShoppingList(menu)
      Then("and a shopping list is printed")
      val expectedShoppingList = """zaterdag:Witlof met kip
zondag:Nasi

zuivel:ei
      geraspte kaas
      vloeibare bakboter
vlees:kipfilet
      kipfilet plakjes
groente:nasi pakket(09-10)
      witlof(08-10)
sauzen:sate saus
pasta:gezeefde tomaten
rijst:kroepoek
      rijst
      rijst
olie:augurken
      zilveruitjes"""
      expectedShoppingList must be === shoppingList.printShoppinglistForUseWhileShopping
    }

    scenario("A menu may contain more than one recipe per day") {
      Given("a menu with dish and dish2 on Saturday and a cookbook with these recipes")
      val menuAsString = """Zaterdag valt op:08102011
      	zaterdag:Witlof met kip
      	zaterdag:Nasi
      """
      val menu = Menu(menuAsString, cookbookClient)
      When("a shoppinglist is generated")
      val shoppingList = new ShoppingList(menu)
      Then("the list contains ingredients for both recipes")
      val expectedShoppingList = """zaterdag:Witlof met kip
zaterdag:Nasi

zuivel:ei
      geraspte kaas
      vloeibare bakboter
vlees:kipfilet
      kipfilet plakjes
groente:nasi pakket(08-10)
      witlof(08-10)
sauzen:sate saus
pasta:gezeefde tomaten
rijst:kroepoek
      rijst
      rijst
olie:augurken
      zilveruitjes"""
      expectedShoppingList must be === shoppingList.printShoppinglistForUseWhileShopping
    }

    scenario("A list of extra's (not related to a recipe) may be added to a menu") {
      Given("a menu with one recipe and a list of extra's")
      val menuAsString = """Zaterdag valt op:08102011
      	zaterdag:dish1

        extra
      	meel:meel
      	zeep:vaatwasmiddel
      """
      val cookBook = """naam:dish1
groente:witlof
"""
      When("a shoppinglist is generated")
      val menuAndList = ShoppingList.split(menuAsString)
      val menu = Menu(menuAndList._1, cookbookClient)
      val extras: List[Ingredient] = Ingredient.readFromText(menuAndList._2)
      val shoppingList = new ShoppingList(menu, extras)
      Then("the list contains witlof and the two extra's")
      val expectedShoppingList = """zaterdag:dish1

groente:witlof(08-10)
meel:meel
zeep:vaatwasmiddel"""
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
