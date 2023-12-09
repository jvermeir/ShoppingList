package nl.vermeir.shopapi

import java.time.LocalDate
import java.util.*

val march10th = LocalDate.parse("2022-03-10")
val menu1 = Menu(id = UUID.fromString("0797c413-45d7-412a-a4da-7ccd90ded9ee"), firstDay = march10th)
val inputMenu1 = Menu(firstDay = march10th)
val category1 =
  Category(id = UUID.fromString("0797c413-45d7-412a-a4da-7ccd90ded9ee"), name = "cat1", shopOrder = 1)
val ingredient1 =
  Ingredient(id = UUID.fromString("0797c413-45d7-412a-a4da-7ccd90ded9ee"), name = "ing1", categoryId = category1.id!!)
val recipe1 =
  Recipe(id = UUID.fromString("0797c413-45d7-412a-a4da-7ccd90ded9ee"), name = "r1", favorite = true)
val recipeIngredient1 = RecipeIngredient(
  id = UUID.fromString("0797c413-45d7-412a-a4da-7ccd90ded9ee"),
  recipeId = recipe1.id!!,
  ingredientId = ingredient1.id!!,
  amount = 1.0f, unit = "kg"
)
val menuItem1 =
  MenuItem(
    id = UUID.fromString("0797c413-45d7-412a-a4da-7ccd90ded9ee"),
    menuId = menu1.id!!,
    recipeId = recipe1.id!!,
    theDay = march10th
  )
