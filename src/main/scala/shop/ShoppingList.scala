package shop
import org.joda.time.DateTime

/**
 * ShoppingList creates a list of groceries sorted by category for a given Menu.
 */
class ShoppingList(menu: Menu) {
  var theDate = menu.dateOfSaturday.minusDays(1)
  val shoppingListItems =
    for { recipe <- menu.recipes } yield { theDate = theDate.plusDays(1); getShoppingListItemsWithDateAdded(recipe, theDate) }
  val shoppingListItemsSortedByCategory = shoppingListItems.flatten.sort(_ < _)

  /*
   * Construct the list of ShoppingListItems by combining a Recipe and a date.
   */
  def getShoppingListItemsWithDateAdded(recipe: Recipe, date: DateTime): List[ShoppingListItem] = {
    def recursiveAdd(ingredients: List[Ingredient]): List[ShoppingListItem] = {
      ingredients match {
        case Nil => List()
        case head :: tail => new ShoppingListItem(head, date) :: recursiveAdd(tail)
      }
    }
    recursiveAdd(recipe.ingredients)
  }
  
  def printShoppinglistForUseWhileShopping: String =
    menu.printMenu + "\n" + printShoppinglistButSkipDuplicateCategoryLables

  def printShoppinglistButSkipDuplicateCategoryLables: String = {
    var list: String = ""
    var currentCategory: String = ""
    var label = ""
    for (ingredient <- shoppingListItemsSortedByCategory) {
      label = if (currentCategory.equals(ingredient.category)) "   " else ingredient.category + ":"
      currentCategory = ingredient.category
      list = list + label + ingredient + "\n"
    }
    list
  }

  def printShoppinglist: String = {

    var list: String = ""
    for (ingredient <- shoppingListItemsSortedByCategory)
      list = list + ingredient + "\n"
    list
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