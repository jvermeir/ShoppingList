package rest

import shop.{Category, Ingredient, Recipe}
import spray.json.{DefaultJsonProtocol, RootJsonFormat}

trait JsonFormats extends DefaultJsonProtocol {
  implicit val categoryJsonFormat: RootJsonFormat[Category] = jsonFormat2(Category.apply)
  implicit val ingredientJsonFormat: RootJsonFormat[Ingredient] = jsonFormat2(Ingredient.apply)
  implicit val recipeJsonFormat: RootJsonFormat[Recipe] = jsonFormat2(Recipe.apply)
}
