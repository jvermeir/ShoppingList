package shop

import org.joda.time.DateTime
import org.scalatest.GivenWhenThen
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.must.Matchers.convertToAnyMustWrapper
import shop.Main.split

class ShoppingListTest extends AnyFlatSpec with GivenWhenThen {

  CategoryService.config("data/test/categoryDatabase.csv")
  CookBookService.config("data/test/cookBookForReadFromFileScenario.txt")

  "Shoppinglist" should "parse a list of groceries per recipe from a text file" in {
    Given("A list of ingredients as strings for the witlof recipe")
    val witlofIngredientsAsText =
      """naam:Witlof met kip
      				vlees:kipfilet plakjes
      				pasta:gezeefde tomaten
      				rijst:rijst
      				diepvries:
      				groente:witlof
      				zuivel:geraspte kaas"""
    When("parse is invoked")
    val witlofRecipe = RecipeO(witlofIngredientsAsText)
    Then("a list of groceries ordered by category is returned and the recipe's name is 'Witlof met kip'")
    val listOfExpectedIngredients = List(
      IngredientO("zuivel", "geraspte kaas"),
      IngredientO("vlees", "kipfilet plakjes"),
      IngredientO("groente", "witlof"),
      IngredientO("pasta", "gezeefde tomaten"),
      IngredientO("rijst", "rijst"))
    val expectedWitlofRecipe = new Recipe("Witlof met kip", listOfExpectedIngredients)
    expectedWitlofRecipe mustBe witlofRecipe
  }

  it should "A menu with witlof on Saturday and Nasi on Sunday results in a list of ingredients" in {
    Given("a menu with witlof on Saturday and Nasi on Sunday and a cookbook with these recipes")
    val menuAsString =
      """Zaterdag valt op:08102011
      	zaterdag:Witlof met kip
      	zondag:Nasi
        """
    When("a menu is generated")
    val menu = Menu(menuAsString)
    val extras: List[Ingredient] = List()
    val shoppingList = new ShoppingList(menu, extras)
    Then("the list of ingredients on the shopping list equals the list of ingredients from both categories combined ordered by category name")
    val expectedListOfIngredients = List(
      ShoppingListItem(IngredientO("zuivel", "ei"), new DateTime(2011, 10, 9, 0, 0)),
      ShoppingListItem(IngredientO("zuivel", "geraspte kaas"), new DateTime(2011, 10, 8, 0, 0)),
      ShoppingListItem(IngredientO("zuivel", "vloeibare bakboter"), new DateTime(2011, 10, 9, 0, 0)),
      ShoppingListItem(IngredientO("vlees", "kipfilet"), new DateTime(2011, 10, 9, 0, 0)),
      ShoppingListItem(IngredientO("vlees", "kipfilet plakjes"), new DateTime(2011, 10, 8, 0, 0)),
      ShoppingListItem(IngredientO("groente", "nasi pakket"), new DateTime(2011, 10, 9, 0, 0)),
      ShoppingListItem(IngredientO("groente", "witlof"), new DateTime(2011, 10, 8, 0, 0)),
      ShoppingListItem(IngredientO("sauzen", "sate saus"), new DateTime(2011, 10, 9, 0, 0)),
      ShoppingListItem(IngredientO("pasta", "gezeefde tomaten"), new DateTime(2011, 10, 8, 0, 0)),
      ShoppingListItem(IngredientO("rijst", "kroepoek"), new DateTime(2011, 10, 9, 0, 0)),
      ShoppingListItem(IngredientO("rijst", "rijst"), new DateTime(2011, 10, 8, 0, 0)),
      ShoppingListItem(IngredientO("rijst", "rijst"), new DateTime(2011, 10, 9, 0, 0)),
      ShoppingListItem(IngredientO("olie", "augurken"), new DateTime(2011, 10, 9, 0, 0)),
      ShoppingListItem(IngredientO("olie", "zilveruitjes"), new DateTime(2011, 10, 9, 0, 0)))
    val expectedIngredientsSortedByCategory = expectedListOfIngredients.sortWith(_ < _)
    val aap = shoppingList.shoppingListItemsSortedByCategory
    expectedIngredientsSortedByCategory mustBe shoppingList.shoppingListItemsSortedByCategory
  }

  it should "A menu with witlof on Sunday and Nasi on Monday results in a list of groceries" in {
    Given("a menu with witlof on Sunday and Nasi on Monday and a kookbook with these recipes")
    val menuAsString =
      """Zaterdag valt op:08102011
      	zondag:Witlof met kip
      	maandag:Nasi
        """
    When("a menu is generated")
    val menu = Menu(menuAsString)
    val extras: List[Ingredient] = List()
    val shoppingList = new ShoppingList(menu, extras)
    Then("a shopping list is produced")
    val expectedShoppingList =
      """ei
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

  it should "A menu and list of groceries are printed for use while shopping" in {
    Given("a menu with witlof on Saturday and Nasi on Sunday and a kookbook with these recipes")
    val menuAsString =
      """Zaterdag valt op:08102011
      	zaterdag:Witlof met kip
      	zondag:Nasi
        """
    When("a menu is generated")
    val menu = Menu(menuAsString)
    val extras: List[Ingredient] = List()
    val shoppingList = new ShoppingList(menu, extras)
    Then("and a shopping list is printed")
    val expectedShoppingList =
      """8 zaterdag:Witlof met kip
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

  it should "A menu may contain more than one recipe per day" in {
    Given("a menu with dish and dish2 on Saturday and a cookbook with these recipes")
    val menuAsString =
      """Zaterdag valt op:08102011
      	zaterdag:Witlof met kip
      	zaterdag:Nasi
        """
    val menu = Menu(menuAsString)
    When("a shoppinglist is generated")
    val extras: List[Ingredient] = List()
    val shoppingList = new ShoppingList(menu, extras)
    Then("the list contains ingredients for both recipes")
    val expectedShoppingList =
      """8 zaterdag:Witlof met kip
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

  it should "A list of extra's (not related to a recipe) may be added to a menu" in {
    Given("a menu with one recipe and a list of extra's")
    val menuAsString =
      """Zaterdag valt op:08102011
      	zaterdag:dish1

        extra
      	meel:meel
      	zeep:vaatwasmiddel
        """
    When("a shoppinglist is generated")
    val menuAndListOfExtras = split(menuAsString)
    val menu = Menu(menuAndListOfExtras._1)
    val extras: List[Ingredient] = IngredientO.readFromText(menuAndListOfExtras._2)
    val shoppingList = new ShoppingList(menu, extras)
    val theList = shoppingList.printShoppinglistForUseWhileShopping
    Then("the list contains witlof and the two extra's")
    val expectedShoppingList =
      """8 zaterdag:dish1

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

  it should "A list of recipes for the week is added to the shopping list" in {
    Given("a menu with two recipes")
    val menuAsString =
      """Zaterdag valt op:08102011
           zaterdag:dish1
           zondag:dish2
        """
    When("a shoppinglist is generated")
    val menuAndListOfExtras = split(menuAsString)
    val menu = Menu(menuAndListOfExtras._1)
    val extras: List[Ingredient] = IngredientO.readFromText(menuAndListOfExtras._2)
    val shoppingList = new ShoppingList(menu, extras)
    Then("the list contains two recipes")
    val expectedShoppingList =
      """8 zaterdag:dish1
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

  it should "A menu may start on Wednesday" in {
    Given("a menu with witlof on Friday and Nasi on Saturday and a kookbook with these recipes")
    val menuAsString =
      """Vrijdag valt op:29052020
      	vrijdag:Witlof met kip
      	zaterdag:Nasi
        """
    When("a menu is generated")
    val menuAndListOfExtras = split(menuAsString)
    val menu = Menu(menuAndListOfExtras._1)
    val extras: List[Ingredient] = IngredientO.readFromText(menuAndListOfExtras._2)
    val shoppingList = new ShoppingList(menu, extras)
    Then("and a shopping list is printed")
    val expectedShoppingList =
      """29 vrijdag:Witlof met kip
30 zaterdag:Nasi

zuivel:
      ei
      geraspte kaas
      vloeibare bakboter
vlees:
      kipfilet
      kipfilet plakjes
groente:
      nasi pakket(30-05)
      witlof(29-05)
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
vrijdag:name:Witlof met kip
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
}
