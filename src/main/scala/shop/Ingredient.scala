package shop

import scala.language.postfixOps

/**
 * Ingredient represents stuff to buy in a store
 */
case class Ingredient(category: Category, name: String) extends Ordered[Ingredient] {

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

  override def toString: String = s"${category.name}:$name"
}

object  Ingredient {

  /*
   * Create an ingredient from a <category>:<name> pair.
   */
  def readFromLine(ingredientLine: String): Ingredient = {
    val ingredient = ingredientLine.split(":")
    ingredient.length match {
      case 2 => Ingredient.applyFromText(ingredient(0).trim(), ingredient(1).trim())
      case _ => null
    }
  }

  def applyFromText(categoryName:String, name:String): Ingredient = {
    val category: Category = CategoryService.getCategoryByName(categoryName)
    Ingredient(category, name)
  }

  def readFromText(ingredientsAsText: String): List[Ingredient] = {
    ingredientsAsText.split("\n") map readFromLine toList
  }
}
