package shop

case class Ingredient(category: String, name: String) extends Ordered[Ingredient] {

  def compare(that: Ingredient) = {
    category match {
      case that.category => name.compare(that.name)
      case _ => category.compare(that.category)
    }
  }
  override def toString:String = category+":"+name
}

object Ingredient {
  def apply(ingredientLine: String): Ingredient = {
    val ingredient = ingredientLine.split(":")
    ingredient.length match {
      case 2 => new Ingredient(ingredient(0).trim(), ingredient(1).trim())
      case _ => new Ingredient("dummy", "dummy")
    }
  }
}