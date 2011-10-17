package shop

import scala.annotation.tailrec
import org.joda.time.DateTime

/**
 * ShoppingList creates a list of groceries sorted by category for a given Menu.
 */
class ShoppingList(menu: Menu) {
  val shoppingListItemsSortedByCategory = getShoppingListItemsSortedByCategory

  /* Construct the list of ShoppingListItems by combining a Recipe and a date. */
  def getShoppingListItemsWithDateAdded(recipe: Recipe, date: DateTime): List[ShoppingListItem] = {
    @tailrec def recursiveAdd(ingredients: List[Ingredient], result: List[ShoppingListItem]): List[ShoppingListItem] = {
      ingredients match {
        case Nil => result
        case head :: tail => recursiveAdd(tail, new ShoppingListItem(head, date) :: result)
      }
    }
    recursiveAdd(recipe.ingredients, List())
  }

  def getShoppingListItemsSortedByCategory: List[ShoppingListItem] = {
    @tailrec def recursiveAdd(theDate: DateTime, recipes: List[Recipe], result: List[List[ShoppingListItem]]): List[List[ShoppingListItem]] = {
      recipes match {
        case Nil => result
        case head :: tail => recursiveAdd(theDate.plusDays(1), tail, getShoppingListItemsWithDateAdded(head, theDate) :: result)
      }
    }
    recursiveAdd(menu.dateOfSaturday, menu.recipes, List()).flatten.sort(_ < _)
  }

  def printShoppinglistForUseWhileShopping: String =
    menu.printMenu + "\n" + printShoppinglistButSkipDuplicateCategoryLables

  def printShoppinglistButSkipDuplicateCategoryLables: String = {
    @tailrec def recursiveAdd(shopingListItems: List[ShoppingListItem], currentCategory: Category, shoppingListAsString: String): String = {
      shopingListItems match {
        case Nil => shoppingListAsString
        case head :: tail =>
          {
            val label = printLabelIfNecessary(currentCategory, head.category)
            recursiveAdd(tail, head.category, shoppingListAsString + "\n" + label + head)
          }
      }
    }
    recursiveAdd(shoppingListItemsSortedByCategory, null, "")
  }

  def printLabelIfNecessary(currentCategory: Category, newCategory: Category): String = {
    if (currentCategory == null) newCategory.name + ":"
    else {
      if (currentCategory.equals(newCategory)) "      "
      else newCategory.name + ":"
    }
  }

  def printShoppinglist: String = {
    @tailrec def recursivePrint(shoppingListItems: List[ShoppingListItem], printedShoppingListItems: String): String = {
      shoppingListItems match {
        case Nil => printedShoppingListItems.substring(1)
        case head :: tail => recursivePrint(tail, printedShoppingListItems + "\n" + head)
      }
    }
    recursivePrint(shoppingListItemsSortedByCategory, "")
  }
}

object ShoppingList {

  /*
   * See if it works.
   */
  def main(args: Array[String]): Unit = {
    val recepten = """naam:Lasagne met gehakt
vlees:gehakt
saus:gezeefde tomaten
saus:lasagnebladen
diepvries:spinazie
zuivel:geraspte kaas

naam:Wafels met soep en pastasalade
meel:zelfrijzend bakmeel
zuivel:ei (5)
zuivel:melk
meel:soep
groente:sla
groente:tomaat
pasta:fusilli
groente:aubergine
groente:paprika
groente:courgette
pasta:pesto
kruiden:basilicum

naam:Nasi
groente:nasi pakket
vlees:kipfilet
saus:sate saus
pasta:rijst
pasta:kroepoek
meel:augurken
meel:zilveruitjes
zuivel:ei (5)
zuivel:vloeibare bakboter

naam:Roti
saus:roti pannenkoeken
vlees:kip
blikken:sperziebonen
groente:krieltjes

naam:Preitaart
groente:prei
groente:selderij
groente:wortel
vlees:kleine braadworstjes
vlees:kip
diepvries:bladerdeeg

naam:Spaghetti met zalmpakketje
pasta:spaghetti
pasta:pesto
vis:zalm
groente:prei
groente:tomaat
folie:aluminiumfolie

naam:varkenshaas met verse pasta
vlees:varkenshaas
verse pasta:pasta
groente:champignons
saus:roomsaus

naam:niks
saus:-

naam:Pizza
pasta:tapenade
pasta:olijven
groente:champignons
beleg:ham
beleg:salami
meel:anjovis
groente:paprika (2)
groente:aardappels (1/2 kg)
pasta:tomatenpuree
zuivel:geraspte kaas
meel:gist
meel:meel

naam:Vegetarische groenteschotel
groente:champignons
groente:wortel
groente:parika(2)
groente:courgette
pasta:passata(750 cl)
zuivel:geraspte kaas
chips:ongezouten notenmix
pasta:tagliatelle

naam:Omelet
groente:prei
groente:wortel
zuivel:ei(6)
beleg:ham
beleg:salami
zuivel:geraspte kaas

naam:Spinazietaart
diepvries:spinazie
zuivel:mozarella
zuivel:kwark
zuivel:ei (4)
meel:meel
pasta:gedroogde tomaten

naam:Tapas
saus:-

"""
    val menuText = """Zaterdag:Lasagne met gehakt
Zondag:Wafels met soep en pastasalade
Maandag:Nasi
Dinsdag:Roti
Woensdag:Preitaart
Donderdag:Spaghetti met zalmpakketje
Vrijdag:varkenshaas met verse pasta
"""
    val menuText2 = """Zaterdag valt op:15102011
Zaterdag:Omelet
Zondag:niks
Maandag:Spinazietaart
Dinsdag:Tapas
Woensdag:Pizza
Donderdag:Vegetarische groenteschotel
Vrijdag:niks
"""
    val menu = Menu(menuText2, CookBook(recepten))
    val shoppingList = new ShoppingList(menu)
    val theList = shoppingList.printShoppinglistForUseWhileShopping
    println(theList)
  }

}