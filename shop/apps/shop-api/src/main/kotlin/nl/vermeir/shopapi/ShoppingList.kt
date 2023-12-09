package nl.vermeir.shopapi

import nl.vermeir.shopapi.data.OutputCategory
import nl.vermeir.shopapi.data.OutputIngredient
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate
import java.util.*

@RestController
class ShoppingListResource(val shoppingListService: ShoppingListService) {
  @GetMapping("/shoppinglist/firstDay/{firstDay}")
  fun findByFirstDay(@PathVariable(name = "firstDay") firstDay: LocalDate) =
    ResponseEntity.ok(shoppingListService.findByFirstDay(firstDay))
}

@Service
class ShoppingListService(val menuService: MenuService, val uuidGenerator: UUIDGenerator) {
  fun outputIngredientToShoppingListIngredient(ingredient: OutputIngredient): ShoppingListIngredient {
    return ShoppingListIngredient(
      id = UUID.fromString(ingredient.id),
      name = ingredient.name,
      categoryId = UUID.fromString(ingredient.category.id),
      listIngredientId = uuidGenerator.generate()
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

  fun findByFirstDay(firstDay: LocalDate): ShoppingList {
    val menu = menuService.menuDetailsByFirstDay(firstDay)

    val outputIngredientsByCategory = menu.menuItems.map { it.recipe.ingredients }.flatten().let {
      it.groupBy { outputCategoryToCategory(it.category) }
    }

    val ingredientsByCategory = outputIngredientsByCategory.map { (category, ingredients) ->
      category to toShoppingListIngredientList(ingredients)
    }.toMap()

    return ShoppingList(
      id = UUID.fromString(menu.id),
      firstDay = menu.firstDay,
      items = ingredientsByCategory
    )
  }
}

data class ShoppingList(
  var id: UUID? = null,
  var firstDay: LocalDate,
  var items: Map<Category, List<ShoppingListIngredient>>
)

data class ShoppingListIngredient(
  var id: UUID? = null,
  var name: String,
  var categoryId: UUID,
  var listIngredientId: UUID
)
