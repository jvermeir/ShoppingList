package nl.vermeir.shopapi.outputmodel

import kotlinx.serialization.Serializable
import nl.vermeir.shopapi.LocalDateSerializer
import java.time.LocalDate

@Serializable
data class OutputMenu(
  val id: String,
  val firstDay: @Serializable(LocalDateSerializer::class) LocalDate,
  val menuItems: List<OutputMenuItem>
)

@Serializable
data class OutputMenuItem(
  val id: String,
  val theDay: @Serializable(LocalDateSerializer::class) LocalDate,
  val recipe: OutputRecipe,
)

@Serializable
data class OutputRecipe(
  val id: String,
  val name: String,
  val favorite: Boolean,
  val ingredients: List<OutputIngredient>
)

@Serializable
data class OutputIngredient(
  val id: String,
  val name: String,
  val category: OutputCategory,
  val unit: String,
  val amount: Float
)

@Serializable
data class OutputCategory(val id: String, val name: String, val shopOrder: Int)

@Serializable
data class OutputShoppingList(
  var id: String,
  @Serializable(with = LocalDateSerializer::class)
  var firstDay: LocalDate,
  var categories: List<OutputShoppingListCategory>
)

@Serializable
data class OutputShoppingListCategory(
  var id: String,
  var name: String,
  var shopOrder: Int,
  var ingredients: List<OutputShoppingListIngredient>
)

@Serializable
data class OutputShoppingListIngredient(
  var id: String,
  var name: String,
  var amount: Float,
  var unit: String
)
