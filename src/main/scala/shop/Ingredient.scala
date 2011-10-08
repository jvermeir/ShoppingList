package shop

case class Ingredient(categorie: String, naam: String) extends Ordered[Ingredient] {

  def compare(that: Ingredient) = {
    categorie match {
      case that.categorie => naam.compare(that.naam)
      case _ => categorie.compare(that.categorie)
    }
  }
  override def toString:String = categorie+":"+naam
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