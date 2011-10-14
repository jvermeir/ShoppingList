package shop
import org.joda.time.DateTime

/**
 * ShoppingList creates a list of groceries sorted by category for a given Menu.
 */
class ShoppingList(menu: Menu) {
  val shoppingListItemsSortedByCategory = getShoppingListItemsSortedByCategory

  /* Construct the list of ShoppingListItems by combining a Recipe and a date. */
  def getShoppingListItemsWithDateAdded(recipe: Recipe, date: DateTime): List[ShoppingListItem] = {
    def recursiveAdd(ingredients: List[Ingredient]): List[ShoppingListItem] = {
      ingredients match {
        case Nil => List()
        case head :: tail => new ShoppingListItem(head, date) :: recursiveAdd(tail)
      }
    }
    recursiveAdd(recipe.ingredients)
  }

  def getShoppingListItemsSortedByCategory: List[ShoppingListItem] = {
    def recursiveAdd(theDate: DateTime, recipes: List[Recipe]): List[List[ShoppingListItem]] = {
      recipes match {
        case Nil => List()
        case head :: tail => getShoppingListItemsWithDateAdded(head, theDate) :: recursiveAdd(theDate.plusDays(1), tail)
      }
    }
    recursiveAdd(menu.dateOfSaturday, menu.recipes).flatten.sort(_ < _)
  }

  def printShoppinglistForUseWhileShopping: String =
    menu.printMenu + "\n" + printShoppinglistButSkipDuplicateCategoryLables

  def printShoppinglistButSkipDuplicateCategoryLables: String = {
    def recursiveAdd(shopingListItems: List[ShoppingListItem], currentCategory: String): String = {
      shopingListItems match {
        case Nil => ""
        case head :: tail =>
          {
            val label = if (currentCategory.equals(head.category)) "   " else head.category + ":"
            label + head + "\n" + recursiveAdd(tail, head.category)
          }
      }
    }
    recursiveAdd(shoppingListItemsSortedByCategory, "")
  }

  def printShoppinglist: String = {
    def recursivePrint(shoppingListItems: List[ShoppingListItem], printedShoppingListItems: String): String = {
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
"""
    val menuText = """Zaterdag:Lasagne met gehakt
Zondag:Wafels met soep en pastasalade
Maandag:Nasi
Dinsdag:Roti
Woensdag:Preitaart
Donderdag:Spaghetti met zalmpakketje
Vrijdag:varkenshaas met verse pasta
"""
    val menu = Menu(menuText, CookBook(recepten))
    val shoppingList = new ShoppingList(menu)
    val theList = shoppingList.printShoppinglist
    println(theList)
  }

}