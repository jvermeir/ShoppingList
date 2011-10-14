package shop

import org.joda.time.DateTime
import org.joda.time.format._

case class IngredientForMenu(ingredient: Ingredient, date: DateTime) extends Ordered[IngredientForMenu] {
  val name = ingredient.name
  val category = ingredient.category

  def this(ingredient: Ingredient) = this(ingredient, new DateTime(1970, 1, 1, 0, 0))
  def compare(that: IngredientForMenu) = {
    ingredient match {
      case that.ingredient => date.compareTo(that.date)
      case _ => this.ingredient.compare(that.ingredient)
    }
  }

  def equals(other: IngredientForMenu): Boolean = {
    ingredient match {
      case other.ingredient => date == other.date
      case _ => other.equals(ingredient)
    }
  }

  def debugToString: String = {
    "[" + name + "," + category + "," + date + "]"
  }
  override def toString: String = {
    if (category == "groente") name + "(" + printDate(date) + ")"
    else name
  }

  //  def compare(that: IngredientForMenu) = (category + name + date).compare(that.category+that.name + that.date)
  //
  def printDate(date: DateTime): String = {
    val fmt = DateTimeFormat forPattern "dd-MM"
    fmt.print(date)
  }
}