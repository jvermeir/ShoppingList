package shopv1

import scala.language.postfixOps

/**
 * Ingredient represents stuff to buy in a store
 */
case class Ingredient(categoryName: String, name: String)(implicit val config: Config) extends Ordered[Ingredient] {
  val categories = new Categories
  val category = categories.getByName(categoryName)

  /*
   * Sort ingredients by category, name
   */
  def compare(that: Ingredient): Int = {
    if (that != null) {
      category match {
        case that.category => name.compare(that.name)
        case _ => category.compare(that.category)
      }
    } else 1
  }

  override def toString: String = category.name + ":" + name
}

object Ingredient {
  /*
   * Create an ingredient from a <category>:<name> pair.
   */
  def readFromLine(ingredientLine: String)(implicit config:Config): Ingredient = {
    val ingredient = ingredientLine.split(":")
    ingredient.length match {
      case 2 => new Ingredient(ingredient(0).trim(), ingredient(1).trim())
      case _ => null
    }
  }

  def readFromText(ingredientsAsText: String)(implicit config:Config): List[Ingredient] = {
    ingredientsAsText.split("\n") map (readFromLine(_)) toList
  }
}
