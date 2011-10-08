package shop

import org.scalatest.junit.JUnitRunner
import org.scalatest.FeatureSpec
import org.scalatest.GivenWhenThen
import org.scalatest.matchers.MustMatchers
import org.junit.runner.RunWith

@RunWith(classOf[JUnitRunner])
class RecipeTest extends FeatureSpec with GivenWhenThen with MustMatchers {
  feature("Boodschappen can parse a list of groceries per recipe from a text file") {
    info("As a family member")
    info("I want to list recipes as text in a file")
    info("So that I can use 'm to create a week-menu")

    scenario("A recipe for witlof results in a list of groceries sorted by category") {
      given("A list of ingredients as strings for the witlof recipe")
      val witlofIngredientsAsText = """naam:Witlof met kip
      				vlees:kipfilet plakjes
      				saus:gezeefde tomaten
      				basis:rijst
      				diepvries:
      				groente:witlof
      				zuivel:geraspte kaas"""
      when("parse is invoked")
      val witlofRecipe = Recipe(witlofIngredientsAsText)
      then("a list of groceries ordered by category is returned and the recipe's name is 'Witlof met kip'")
      val listOfExpectedIngredients = List(
    		  Ingredient("basis", "rijst"),
        Ingredient("groente", "witlof"),
        Ingredient("saus", "gezeefde tomaten"),
        Ingredient("vlees", "kipfilet plakjes"),
        Ingredient("zuivel", "geraspte kaas"))
      val expectedWitlofRecipe = new Recipe("Witlof met kip", listOfExpectedIngredients)
      expectedWitlofRecipe must be === witlofRecipe
    }

    scenario("A recipe for witlof and a recipe for Nasi result in a kookboek with two recipes and their groceries") {
      given("A list of ingredients as strings for the witlof recipe followed by a list of strings for the Nasi recipe")
      when("a new kookboek is created from the text version")
      val kookboek = CookBook(cookBookAsText)
      then("the kookboek containts a recipe for Witlof and one for Nasi")
      2 must be === kookboek.size
      "Witlof met kip" must be === kookboek.findRecipe("Witlof met kip").name
      "Nasi" must be === kookboek.findRecipe("Nasi").name
    }
  }

  feature("Boodschappen can parse a list of groceries per recipe from a text file") {
    info("As a family member")
    info("I want to specify a menu for a week as a list of day:recipe pairs")
    info("So that I can create a shopping list")

    scenario("A menu with witlof on Sunday and Nasi on Monday results in a list of ingredients") {
      given("a menu with witlof on Sunday and Nasi on Monday and a kookbook with these recipes")
      val menuAsString = """Zondag:Witlof met kip
      	Maandag:Nasi
      """
      when("a menu is generated")
      val menu = Menu(menuAsString, CookBook(cookBookAsText))
      then("the list of ingredients on the shopping list equals the list of ingredients from both categories combined ordered by category name")
      val expectedListOfIngredients = List(
    		  Ingredient("basis", "augurken"),
    		  Ingredient("basis", "kroepoek"),
    		  Ingredient("basis", "rijst"),
    		  Ingredient("basis", "rijst"),
    		  Ingredient("basis", "zilveruitjes"),
        Ingredient("groente", "nasi pakket"),
        Ingredient("groente", "witlof"),
        Ingredient("saus", "gezeefde tomaten"),
        Ingredient("saus", "sate saus"),
        Ingredient("vlees", "kipfilet"),
        Ingredient("vlees", "kipfilet plakjes"),
        Ingredient("zuivel", "ei"),
        Ingredient("zuivel", "geraspte kaas"),
        Ingredient("zuivel", "vloeibare bakboter"))
      expectedListOfIngredients must be === menu.sortedListOfIngredients
    }

    scenario("A menu with witlof on Sunday and Nasi on Monday results in a list of groceries") {
      given("a menu with witlof on Sunday and Nasi on Monday and a kookbook with these recipes")
      val menuAsString = """Zondag:Witlof met kip
      	Maandag:Nasi
      """
      when("a menu is generated")
      val menu = Menu(menuAsString, CookBook(cookBookAsText))
      val boodschappenlijst = menu.printShoppinglist
      then("a shopping list is produced")
      val expectedShoppingList = """basis:augurken
basis:kroepoek
basis:rijst
basis:rijst
basis:zilveruitjes
groente:nasi pakket
groente:witlof
saus:gezeefde tomaten
saus:sate saus
vlees:kipfilet
vlees:kipfilet plakjes
zuivel:ei
zuivel:geraspte kaas
zuivel:vloeibare bakboter
"""
      expectedShoppingList must be === menu.printShoppinglist
    }
  }

  scenario("A menu can be read from a file") {
    given("a menu in a file")
    when("the file is read")
    then("a shoppinglist is generated")
    pending
  }

  scenario("A Kookboek can be read from a file") {
    given("a kookboek in a text file")
    when("the file is read")
    then("a kookboek is created")
    pending
  }

  scenario("A exception is thrown when an unknown recipe is added to a menu") {
    given("a Menu and a Kookboek")
    val menuAsString = """Zondag:Witlof met kip
      	Maandag:Nasi
        Dinsdag:Non existent recipe
      """
    when("the shoppinglist is created")
    then("a exception is thrown")
    intercept[NoSuchElementException] {
      val menu = Menu(menuAsString, CookBook(cookBookAsText))
      menu.printShoppinglist
    }
  }

  scenario("A menu and list of groceries are printed for use while shopping") {
    given("a menu with witlof on Sunday and Nasi on Monday and a kookbook with these recipes")
    val menuAsString = """Zondag:Witlof met kip
      	Maandag:Nasi
      """
    when("a menu is generated")
    val menu = Menu(menuAsString, CookBook(cookBookAsText))
    then("a shopping list is produced")
    val boodschappenlijst = menu.printShoppinglistForUseWhileShopping
    val expectedShoppingList = """
<<TODO: eerst menu, dan boodschappen per categorie, gesorteerd op categorienummer>
groente:nasi pakket
groente:witlof
saus:gezeefde tomaten
saus:sate saus
vlees:kipfilet
vlees:kipfilet plakjes
basis:augurken
basis:kroepoek
basis:rijst
basis:rijst
basis:zilveruitjes
zuivel:ei
zuivel:geraspte kaas
zuivel:vloeibare bakboter
"""
    //      expectedShoppingList must be === menu.printBoodschappenlijst
    pending
  }

  val cookBookAsText = """naam:Witlof met kip
		  vlees:kipfilet plakjes
		  saus:gezeefde tomaten
		  basis:rijst
		  diepvries:
		  groente:witlof
		  zuivel:geraspte kaas
		  
		  
		  
		  naam:Nasi
		  groente:nasi pakket
		  vlees:kipfilet
		  saus:sate saus
		  basis:rijst
		  basis:kroepoek
		  basis:augurken
		  basis:zilveruitjes
		  zuivel:ei
		  zuivel:vloeibare bakboter
		  
		  
		  
		  """
}