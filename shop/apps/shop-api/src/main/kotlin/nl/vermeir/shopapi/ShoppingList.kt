package nl.vermeir.shopapi

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import kotlinx.serialization.Serializable
import nl.vermeir.shopapi.data.OutputMenu
import nl.vermeir.shopapi.data.OutputShoppingList
import nl.vermeir.shopapi.data.OutputShoppingListCategory
import nl.vermeir.shopapi.data.OutputShoppingListIngredient
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
  val ingredientRepository: IngredientRepository,
  val uuidGenerator: UUIDGenerator
) {

  fun getShoppingList(firstDay: LocalDate): OutputShoppingList {
    val shoppingList = shoppingListRepository.findByFirstDay(firstDay)
    return shoppingListToOutputShoppingList(shoppingList)
  }

  fun updateIngredient(id: UUID, ingredientId: UUID, amount: Float): OutputShoppingList {
    val ingredient = shoppingListIngredientRepository.findById(ingredientId)
    ingredient.ifPresentOrElse(
      {
        it.amount = amount
        shoppingListIngredientRepository.save(it)
      },
      {
        val newIngredient = ingredientRepository.findById(ingredientId).get()
        val shoppingListIngredient = ShoppingListIngredient(
          id = newIngredient.id!!,
          name = newIngredient.name,
          ingredientId = ingredientId,
          shoppingListCategoryId = newIngredient.categoryId,
          unit = "kg", // TODO get this from ingredient
          amount = amount
        )
        shoppingListIngredientRepository.save(shoppingListIngredient)
      })
// TODO
    val shoppingList = shoppingListRepository.findById(id).get()
    return shoppingListToOutputShoppingList(shoppingList)
  }

  fun addMenuItem(id: UUID, menuItemId: UUID, day: LocalDate): OutputShoppingList {
    TODO("Not yet implemented")
  }

  fun fromMenu(firstDay: LocalDate): OutputShoppingList {
    val menu = menuService.menuDetailsByFirstDay(firstDay)
    val newShoppingList = menuToShoppingList(menu)
    val shoppingList = shoppingListRepository.save(newShoppingList)
    val newCategories = menu.menuItems.map { it.recipe.ingredients }.flatten().let {
      it.groupBy { it.category }
    }

    val outputShoppingList = shoppingListToOutputShoppingList(newShoppingList)

    val cats = newCategories.map { category ->
      val newCategory = ShoppingListCategory(
        id = uuidGenerator.generate(),
        categoryId = UUID.fromString(category.key.id),
        shoppingListId = shoppingList.id!!,
        name = category.key.name,
        shopOrder = category.key.shopOrder
      )
      val savedCategory = shoppingListCategoriesRepository.save(newCategory)
      // TODO: is this savedCategory.id?.let useful?
      val ingredients = category.value.map { ingredient ->
        val newIngredient = savedCategory.id?.let {
          ShoppingListIngredient(
            id = uuidGenerator.generate(),
            ingredientId = UUID.fromString(ingredient.id),
            shoppingListCategoryId = it,
            name = ingredient.name,
            unit = ingredient.unit,
            amount = ingredient.amount
          )
        }
        // TODO: fix !!
        val savedIngredient = shoppingListIngredientRepository.save(newIngredient!!)
        OutputShoppingListIngredient(
          id = savedIngredient.id.toString(),
          name = savedIngredient.name,
          amount = savedIngredient.amount,
          unit = savedIngredient.unit
        )
      }
      OutputShoppingListCategory(
        id = savedCategory.id.toString(),
        name = savedCategory.name,
        shopOrder = savedCategory.shopOrder,
        ingredients = ingredients
      )
    }
    outputShoppingList.categories = cats
    return outputShoppingList
  }

  fun shoppingListToOutputShoppingList(shoppingList: ShoppingList): OutputShoppingList {
    val categories = shoppingListCategoriesRepository.findByShoppingListId(shoppingList.id!!)
    val outputCategories = categories.map { category ->
      val outputIngredients = shoppingListIngredientRepository.findByShoppingListCategoryId(category.id!!)
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

//  fun outputIngredientToShoppingListIngredient(ingredient: OutputIngredient): ShoppingListIngredient {
//    return ShoppingListIngredient(
//      id = UUID.fromString(ingredient.id),
//      name = ingredient.name,
//      shoppingListCategoryId =  UUID.fromString(ingredient.category.id),
//      unit = ingredient.unit,
//      amount = ingredient.amount
//    )
//  }
//
//  fun outputCategorytoShoppingListCategory(category: OutputCategory, shoppingListId: UUID): ShoppingListCategory {
//    return ShoppingListCategory(
//      id = UUID.fromString(category.id),
//      name = category.name,
//      shopOrder = category.shopOrder,
//      shoppingListId = shoppingListId
//    )
//  }
//
//  fun outputShoppingListToShoppingList(shoppingList: OutputShoppingList): ShoppingList {
//    return ShoppingList(
//      id = UUID.fromString(shoppingList.id),
//      firstDay = shoppingList.firstDay
//    )
//  }
//
//  fun toShoppingListIngredientList(outputIngredients: List<OutputIngredient>): List<ShoppingListIngredient> {
//    return outputIngredients.map { outputIngredientToShoppingListIngredient(it) }
//  }
//
//  fun outputCategoryToCategory(category: OutputCategory): Category {
//    return Category(
//      id = UUID.fromString(category.id),
//      name = category.name,
//      shopOrder = category.shopOrder
//    )
//  }
//
//  fun createForFirstDay(firstDay: LocalDate): OutputShoppingList {
//    val menu = menuService.menuDetailsByFirstDay(firstDay)
//
//    val outputIngredientsByCategory = menu.menuItems.map { it.recipe.ingredients }.flatten().let {
//      it.groupBy { outputCategoryToCategory(it.category) }
//    }
//
//    val ingredientsByCategory = outputIngredientsByCategory.map { (category, ingredients) ->
//      category.id.toString() to toShoppingListIngredientList(ingredients)
//    }.toMap()
//
//    return OutputShoppingList(
//      id = menu.id,
//      firstDay = menu.firstDay,
//      categories = emptyList()
//    )
//  }

}

interface ShoppingListRepository : CrudRepository<ShoppingList, UUID> {
  fun findByFirstDay(firstDay: LocalDate): ShoppingList
}

interface ShoppingListCategoriesRepository : CrudRepository<ShoppingListCategory, UUID> {
  fun findByShoppingListId(shoppingListId: UUID): List<ShoppingListCategory>
}

interface ShoppingListIngredientRepository : CrudRepository<ShoppingListIngredient, UUID> {
  fun findByShoppingListCategoryId(categoryId: UUID): List<ShoppingListIngredient>
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
