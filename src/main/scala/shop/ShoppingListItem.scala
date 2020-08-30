package shop

/**
 * A ShoppingListItem is a Ingredient extended with date it will be used on
 */

import java.time.LocalDate
import java.time.format.DateTimeFormatter

import shopv1.PanicException

case class ShoppingListItem(ingredient: Ingredient, date: LocalDate) extends Ordered[ShoppingListItem] {
  if (ingredient == null) throw new PanicException("Ingredient was null")
  def name: String = ingredient.name
  def category: Category = ingredient.category

  /*
   * If the date doesn't really matter it is filled with Jan 1st 1970. 
   * TODO: think of some better solution. 
   */
  def this(ingredient: Ingredient) = this(ingredient, LocalDate.now)

  /*
   * Sort shoppingListItems by Ingredient and date
   */
  def compare(that: ShoppingListItem): Int = {
    ingredient match {
      case that.ingredient => date.compareTo(that.date)
      case null => -1
      case _ if that != null => this.ingredient.compare(that.ingredient)
      case _ => -1
    }
  }

  def debugToString: String = s"[$name,$category,$date]"

  override def toString: String = {
    if (category.name == "groente" || category.name == "sla") {
      name + "(" + printDate(date) + ")"
    }
    else name
  }

  val ddMMFormatter = DateTimeFormatter.ofPattern("dd-MM")

  def printDate(date: LocalDate): String = ddMMFormatter.format(date)
}
