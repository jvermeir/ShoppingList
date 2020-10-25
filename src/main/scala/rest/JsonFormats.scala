package rest

import java.time.LocalDateTime

import shop.Dates.{dateToIsoString, parseIsoDateString}
import shop._
import spray.json.{DefaultJsonProtocol, JsString, JsValue, JsonFormat, RootJsonFormat, deserializationError}

object DateMarshalling {

  implicit object DateFormat extends JsonFormat[LocalDateTime] {
    def write(date: LocalDateTime): JsString = JsString(dateToIsoString(date))

    def read(json: JsValue): LocalDateTime = json match {
      case JsString(rawDate) =>
        parseIsoDateString(rawDate)
          .fold(deserializationError(s"Expected ISO Date format, got $rawDate"))(identity)
      case error => deserializationError(s"Expected JsString, got $error")
    }
  }

}

trait JsonFormats extends DefaultJsonProtocol {

  import DateMarshalling._

  implicit val categoryJsonFormat: RootJsonFormat[Category] = jsonFormat2(Category.apply)
  implicit val ingredientJsonFormat: RootJsonFormat[Ingredient] = jsonFormat2(Ingredient.apply)
  implicit val recipeJsonFormat: RootJsonFormat[Recipe] = jsonFormat2(Recipe.apply)
  implicit val menuItemJsonFormat: RootJsonFormat[MenuItem] = jsonFormat(MenuItem.apply, "id", "date", "dayOfWeek", "recipe")
  implicit val menuJsonFormat: RootJsonFormat[Menu] = jsonFormat(Menu.apply, "menuItems", "startOfPeriod")
  implicit val shoppingListJsonFormat: RootJsonFormat[ShoppingList] = jsonFormat(ShoppingList.apply, "menu", "extras")
}

