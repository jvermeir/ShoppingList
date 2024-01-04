package nl.vermeir.shopapi.shoppinglist

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.transaction.Transactional
import kotlinx.serialization.Serializable
import nl.vermeir.shopapi.LocalDateSerializer
import nl.vermeir.shopapi.UUIDSerializer
import org.springframework.data.repository.CrudRepository
import java.time.LocalDate
import java.util.*

interface ShoppingListRepository : CrudRepository<ShoppingList, UUID> {
  fun findByFirstDay(firstDay: LocalDate): ShoppingList?
}

interface ShoppingListCategoriesRepository : CrudRepository<ShoppingListCategory, UUID> {
  fun findByShoppingListId(shoppingListId: UUID): List<ShoppingListCategory>
  fun findByCategoryId(categoryId: UUID): Optional<ShoppingListCategory>
}

interface ShoppingListIngredientRepository : CrudRepository<ShoppingListIngredient, UUID> {
  @Transactional
  fun deleteByShoppingListCategoryId(shoppingListCategoryId: UUID)
  fun findByShoppingListCategoryId(categoryId: UUID): List<ShoppingListIngredient>
  fun findByIngredientId(ingredientId: UUID): Optional<ShoppingListIngredient>
}

@Entity(name = "SHOPPING_LISTS")
@Serializable
data class ShoppingList(
  @jakarta.persistence.Id @GeneratedValue
  @Serializable(with = UUIDSerializer::class)
  var id: UUID? = null,
  @Serializable(with = LocalDateSerializer::class)
  var firstDay: LocalDate,
)

object EmptyShoppingList :
  ShoppingList(id = UUID.fromString("11111111-1111-1111-1111-111111111111"), firstDay = LocalDate.MIN)

@Entity(name = "SHOPPING_LIST_CATEGORIES")
@Serializable
data class ShoppingListCategory(
  @jakarta.persistence.Id @GeneratedValue
  @Serializable(with = UUIDSerializer::class)
  var id: UUID? = null,
  @Serializable(with = UUIDSerializer::class)
  var categoryId: UUID,
  @Serializable(with = UUIDSerializer::class)
  var shoppingListId: UUID,
  var name: String,
  var shopOrder: Int,
)

@Entity(name = "SHOPPING_LIST_INGREDIENTS")
@Serializable
data class ShoppingListIngredient(
  @jakarta.persistence.Id @GeneratedValue
  @Serializable(with = UUIDSerializer::class)
  var id: UUID? = null,
  @Serializable(with = UUIDSerializer::class)
  var ingredientId: UUID,
  var name: String,
  @Serializable(with = UUIDSerializer::class)
  var shoppingListCategoryId: UUID,
  var unit: String,
  var amount: Float
)
