package nl.vermeir.shopapi

import jakarta.persistence.Entity
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
      { shoppingListIngredient ->
        if (amount == 0f) {
          shoppingListIngredientRepository.delete(shoppingListIngredient)
          shoppingListCategoriesRepository.findById(shoppingListIngredient.shoppingListCategoryId).ifPresent {
            val ingredients = shoppingListIngredientRepository.findByShoppingListCategoryId(it.id!!)
            if (ingredients.isEmpty()) {
              shoppingListCategoriesRepository.delete(it)
            }
          }
        } else {
          shoppingListIngredient.amount = amount
          shoppingListIngredientRepository.save(shoppingListIngredient)
        }
      },
      {
        val newIngredient = ingredientRepository.findById(ingredientId).get()
        val shoppingListCategory = shoppingListCategoriesRepository.findByCategoryId(newIngredient.categoryId).get()
        val shoppingListIngredient = ShoppingListIngredient(
          id = newIngredient.id!!,
          name = newIngredient.name,
          ingredientId = ingredientId,
          shoppingListCategoryId = shoppingListCategory.id!!,
          unit = newIngredient.unit,
          amount = amount
        )
        shoppingListIngredientRepository.save(shoppingListIngredient)
      })

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

    val categories = newCategories.map { category ->
      val savedCategory = shoppingListCategoriesRepository.save(createShoppingListCategory(category, shoppingList))
      val ingredientsSummed = sumIngredients(category)
      val ingredients = saveIngredients(ingredientsSummed, savedCategory)

      OutputShoppingListCategory(
        id = savedCategory.id.toString(),
        name = savedCategory.name,
        shopOrder = savedCategory.shopOrder,
        ingredients = ingredients
      )
    }
    outputShoppingList.categories = categories
    return outputShoppingList
  }

  private fun saveIngredients(
    ingredients: List<OutputIngredient>,
    category: ShoppingListCategory
  ): List<OutputShoppingListIngredient> {
    val shoppingListIngredients = ingredients.map { ingredient ->
      val newIngredient = category.id.let {
        ShoppingListIngredient(
          id = uuidGenerator.generate(),
          ingredientId = UUID.fromString(ingredient.id),
          shoppingListCategoryId = it!!,
          name = ingredient.name,
          unit = ingredient.unit,
          amount = ingredient.amount
        )
      }

      val savedIngredient = shoppingListIngredientRepository.save(newIngredient)

      OutputShoppingListIngredient(
        id = savedIngredient.id.toString(),
        name = savedIngredient.name,
        amount = savedIngredient.amount,
        unit = savedIngredient.unit
      )
    }
    return shoppingListIngredients
  }

  private fun createShoppingListCategory(
    category: Map.Entry<OutputCategory, List<OutputIngredient>>,
    shoppingList: ShoppingList
  ): ShoppingListCategory {
    return ShoppingListCategory(
      id = uuidGenerator.generate(),
      categoryId = UUID.fromString(category.key.id),
      shoppingListId = shoppingList.id!!,
      name = category.key.name,
      shopOrder = category.key.shopOrder
    )
  }

  // TODO: test
  private fun sumIngredients(category: Map.Entry<OutputCategory, List<OutputIngredient>>): List<OutputIngredient> {
    val ingredientsPerCategory: List<OutputIngredient> = category.value
    val ingredientsById: Map<String, List<OutputIngredient>> = ingredientsPerCategory.let { it.groupBy { it.id } }
    val sumAmountByIngredientId = ingredientsById.map { (id, ings) -> id to ings.sumOf { it.amount.toDouble() } }
    return sumAmountByIngredientId.map { (id, amount) ->
      val theIngredient: OutputIngredient = ingredientsPerCategory.first { it.id == id }
      theIngredient.copy(amount = amount.toFloat())
    }
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
}

interface ShoppingListRepository : CrudRepository<ShoppingList, UUID> {
  fun findByFirstDay(firstDay: LocalDate): ShoppingList
}

interface ShoppingListCategoriesRepository : CrudRepository<ShoppingListCategory, UUID> {
  fun findByShoppingListId(shoppingListId: UUID): List<ShoppingListCategory>
  fun findByCategoryId(categoryId: UUID): Optional<ShoppingListCategory>
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
