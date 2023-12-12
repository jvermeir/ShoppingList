package nl.vermeir.shopapi

import jakarta.persistence.GeneratedValue
import kotlinx.serialization.Serializable
import nl.vermeir.shopapi.data.*
import org.springframework.data.repository.CrudRepository
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.*
import java.time.LocalDate
import java.util.*

@RestController
class ShoppingListResource(val shoppingListService: ShoppingListService) {

  @PostMapping("/shoppinglist/frommenu/firstDay/{firstDay}")
  fun fromMenu(@PathVariable(name = "firstDay") firstDay: LocalDate) =
    ResponseEntity.ok(shoppingListService.fromMenu(firstDay))

  @GetMapping("/shoppinglist/firstDay/{firstDay}")
  fun findByFirstDay(@PathVariable(name = "firstDay") firstDay: LocalDate) =
    ResponseEntity.ok(shoppingListService.getShoppingList(firstDay))

  @PutMapping("shoppinglist/{id}/ingredient/{ingredientId}/amount/{amount}")
  fun updateAmount(
    @PathVariable(name = "id") id: UUID,
    @PathVariable(name = "ingredientId") ingredientId: UUID,
    @PathVariable(name = "amount") amount: Float
  ) =
    ResponseEntity.ok(shoppingListService.updateIngredient(id, ingredientId, amount))

  @PutMapping("shoppinglist/{id}/menuItem/{menuItemId}/day/{day}")
  fun addMenuItem(
    @PathVariable(name = "id") id: UUID,
    @PathVariable(name = "menuItemId") menuItemId: UUID,
    day: LocalDate
  ) =
    ResponseEntity.ok(shoppingListService.addMenuItem(id, menuItemId, day))
}

@Service
class ShoppingListService(
  val menuService: MenuService,
  val shoppingListRepository: ShoppingListRepository,
  val shoppingListCategoriesRepository: ShoppingListCategoriesRepository,
  val shoppingListIngredientRepository: ShoppingListIngredientRepository,
  val uuidGenerator: UUIDGenerator
) {

  fun getShoppingList(firstDay: LocalDate): OutputShoppingList {
    TODO("Not yet implemented")
  }

  fun updateIngredient(id: UUID, ingredientId: UUID, amount: Float): OutputShoppingList {
    TODO("Not yet implemented")
  }

  fun addMenuItem(id: UUID, menuItemId: UUID, day: LocalDate): OutputShoppingList {
    TODO("Not yet implemented")
  }

  fun fromMenu(firstDay: LocalDate): OutputShoppingList {
    val menu = menuService.menuDetailsByFirstDay(firstDay)
    val shoppingList = menuToShoppingList(menu)
    shoppingListRepository.save(shoppingList)
    return shoppingListToOutputShoppingList(shoppingList)
  }

  private fun shoppingListToOutputShoppingList(shoppingList: ShoppingList): OutputShoppingList {
    val categories = shoppingListCategoriesRepository.findByShoppingListId(shoppingList.id!!)
    val outputCategories = categories.map { category ->
      val outputIngredients = shoppingListIngredientRepository.findByCategoryId(category.id!!)
        .map { ingredient ->
          OutputShoppingListIngredient(
            id = ingredient.id.toString(),
            name = ingredient.name,
            amount = ingredient.amount,
            unit = ingredient.unit
          )
        }

      OutputShoppingListCategory(
        id = category.id.toString(),
        name = category.name,
        shopOrder = category.shopOrder,
        ingredients = outputIngredients
      )
    }

    return OutputShoppingList(
      id = shoppingList.id.toString(),
      firstDay = shoppingList.firstDay,
      categories = outputCategories
    )
  }

  private fun menuToShoppingList(menu: OutputMenu): ShoppingList {
    return ShoppingList(
      id = uuidGenerator.generate(),
      firstDay = menu.firstDay
    )
  }


  // TODO

  fun outputIngredientToShoppingListIngredient(ingredient: OutputIngredient): ShoppingListIngredient {
    return ShoppingListIngredient(
      id = UUID.fromString(ingredient.id),
      name = ingredient.name,
      categoryId = UUID.fromString(ingredient.category.id),
      unit = ingredient.unit,
      amount = ingredient.amount
    )
  }

  fun outputCategorytoShoppingListCategory(category: OutputCategory, shoppingListId: UUID): ShoppingListCategory {
    return ShoppingListCategory(
      id = UUID.fromString(category.id),
      name = category.name,
      shopOrder = category.shopOrder,
      shoppingListId = shoppingListId
    )
  }

  fun outputShoppingListToShoppingList(shoppingList: OutputShoppingList): ShoppingList {
    return ShoppingList(
      id = UUID.fromString(shoppingList.id),
      firstDay = shoppingList.firstDay
    )
  }

  fun toShoppingListIngredientList(outputIngredients: List<OutputIngredient>): List<ShoppingListIngredient> {
    return outputIngredients.map { outputIngredientToShoppingListIngredient(it) }
  }

  fun outputCategoryToCategory(category: OutputCategory): Category {
    return Category(
      id = UUID.fromString(category.id),
      name = category.name,
      shopOrder = category.shopOrder
    )
  }

  fun createForFirstDay(firstDay: LocalDate): OutputShoppingList {
    val menu = menuService.menuDetailsByFirstDay(firstDay)

    val outputIngredientsByCategory = menu.menuItems.map { it.recipe.ingredients }.flatten().let {
      it.groupBy { outputCategoryToCategory(it.category) }
    }

    val ingredientsByCategory = outputIngredientsByCategory.map { (category, ingredients) ->
      category.id.toString() to toShoppingListIngredientList(ingredients)
    }.toMap()

    return OutputShoppingList(
      id = menu.id,
      firstDay = menu.firstDay,
      categories = emptyList()
    )
  }

}

interface ShoppingListRepository : CrudRepository<ShoppingList, UUID> {
}

interface ShoppingListCategoriesRepository : CrudRepository<ShoppingListCategory, UUID> {
  fun findByShoppingListId(shoppingListId: UUID): List<ShoppingListCategory>
}

interface ShoppingListIngredientRepository : CrudRepository<ShoppingListIngredient, UUID> {
  fun findByCategoryId(categoryId: UUID): List<ShoppingListIngredient>
}

@Serializable
data class ShoppingList(
  @jakarta.persistence.Id @GeneratedValue
  @Serializable(with = UUIDSerializer::class)
  var id: UUID? = null,
  @Serializable(with = LocalDateSerializer::class)
  var firstDay: LocalDate,
)

data class ShoppingListCategory(
  @jakarta.persistence.Id @GeneratedValue
  @Serializable(with = UUIDSerializer::class)
  var id: UUID? = null,
  var shoppingListId: UUID,
  var name: String,
  var shopOrder: Int,
)

@Serializable
data class ShoppingListIngredient(
  @jakarta.persistence.Id @GeneratedValue
  @Serializable(with = UUIDSerializer::class)
  var id: UUID? = null,
  var name: String,
  @Serializable(with = UUIDSerializer::class)
  var categoryId: UUID,
  var unit: String,
  var amount: Float
)
