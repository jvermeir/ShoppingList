package nl.vermeir.shopapi

import java.time.LocalDate
import java.util.*

val theId = "cb494d70-5c5c-45d4-ac66-9b84fe096fc6"
val march10th = LocalDate.parse("2022-03-10")
val menu1 = Menu(id = UUID.fromString(theId), firstDay = march10th)
val inputMenu1 = Menu(firstDay = march10th)
val category1 =
  Category(id = UUID.fromString(theId), name = "cat1", shopOrder = 1)
val ingredient1 =
  Ingredient(id = UUID.fromString(theId), name = "ing1", categoryId = category1.id!!)
val recipe1 =
  Recipe(id = UUID.fromString(theId), name = "r1", favorite = true)
val recipeIngredient1 = RecipeIngredient(
  id = UUID.fromString(theId),
  recipeId = recipe1.id!!,
  ingredientId = ingredient1.id!!,
  amount = 1.0f, unit = "kg"
)
val menuItem1 =
  MenuItem(
    id = UUID.fromString(theId),
    menuId = menu1.id!!,
    recipeId = recipe1.id!!,
    theDay = march10th
  )
val shoppingList1 = ShoppingList(
  id = UUID.fromString(theId),
  firstDay = march10th
)
val shoppingListCategory1 = ShoppingListCategory(
  id = UUID.fromString(theId),
  name = "cat1",
  shopOrder = 1,
  shoppingListId = shoppingList1.id!!
)
