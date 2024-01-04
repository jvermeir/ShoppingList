package nl.vermeir.shopapi.shoppinglist

import nl.vermeir.shopapi.CategoryRepository
import nl.vermeir.shopapi.IngredientRepository
import nl.vermeir.shopapi.MenuService
import nl.vermeir.shopapi.UUIDGenerator
import nl.vermeir.shopapi.data.*
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.util.*

@Service
class ShoppingListService(
  val menuService: MenuService,
  val shoppingListRepository: ShoppingListRepository,
  val shoppingListCategoriesRepository: ShoppingListCategoriesRepository,
  val shoppingListIngredientRepository: ShoppingListIngredientRepository,
  val ingredientRepository: IngredientRepository,
  val categoryRepository: CategoryRepository,
  val uuidGenerator: UUIDGenerator
) {

  fun getShoppingList(firstDay: LocalDate): OutputShoppingList {
    val shoppingList =
      shoppingListRepository.findByFirstDay(firstDay) ?: EmptyShoppingList
    return shoppingListToOutputShoppingList(shoppingList)
  }

  fun updateIngredient(shoppingListId: UUID, ingredientId: UUID, amount: Float): OutputShoppingList {
    val ingredient = shoppingListIngredientRepository.findById(ingredientId)
    ingredient.ifPresentOrElse(
      { shoppingListIngredient ->
        updateAmount(amount, shoppingListIngredient)
      },
      {
        createShoppingListIngredient(shoppingListId, ingredientId, amount)
      })

    val shoppingList = shoppingListRepository.findById(shoppingListId).get()
    return shoppingListToOutputShoppingList(shoppingList)
  }

  fun addIngredient(shoppingListId: UUID, ingredientId: UUID, amount: Float): OutputShoppingList {
    val ingredient = shoppingListIngredientRepository.findByIngredientId(ingredientId)
    ingredient.ifPresentOrElse(
      { shoppingListIngredient: ShoppingListIngredient ->
        updateAmount(amount, shoppingListIngredient)
      },
      {
        createShoppingListIngredient(shoppingListId, ingredientId, amount)
      })

    val shoppingList = shoppingListRepository.findById(shoppingListId).get()
    return shoppingListToOutputShoppingList(shoppingList)
  }

  fun addMenuItem(id: UUID, menuItemId: UUID, day: LocalDate): OutputShoppingList {
    TODO("Not yet implemented")
  }

  fun reloadFromMenu(firstDay: LocalDate): OutputShoppingList {
    shoppingListRepository.findByFirstDay(firstDay)?.let {
      shoppingListCategoriesRepository.findByShoppingListId(it.id!!)
        .forEach { cat -> deleteCategory(it.id!!, cat.id!!) }
      shoppingListRepository.deleteById(it.id!!)
    }
    return fromMenu(firstDay)
  }

  fun fromMenu(firstDay: LocalDate): OutputShoppingList {
    shoppingListRepository.findByFirstDay(firstDay)?.let {
      return shoppingListToOutputShoppingList(it)
    }
    val menu = menuService.menuDetailsByFirstDay(firstDay)
    val newShoppingList = menuToShoppingList(menu)
    val shoppingList = shoppingListRepository.save(newShoppingList)
    val newCategories = menu.menuItems.map { it.recipe.ingredients }.flatten().let {
      it.groupBy { ingredient -> ingredient.category }
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

  fun deleteCategory(shoppingListId: UUID, categoryId: UUID): ShoppingList {
    shoppingListIngredientRepository.deleteByShoppingListCategoryId(categoryId)
    shoppingListCategoriesRepository.deleteById(categoryId)
    return shoppingListRepository.findById(shoppingListId).get()
  }

  fun deleteIngredient(shoppingListId: UUID, ingredientId: UUID): ShoppingList {
    shoppingListIngredientRepository.deleteById(ingredientId)
    return shoppingListRepository.findById(shoppingListId).get()
  }

  private fun saveIngredients(
    ingredients: List<OutputIngredient>,
    category: ShoppingListCategory
  ): List<OutputShoppingListIngredient> {
    val shoppingListIngredients = ingredients.map { ingredient ->
      val newIngredient = ShoppingListIngredient(
        id = uuidGenerator.generate(),
        ingredientId = UUID.fromString(ingredient.id),
        shoppingListCategoryId = category.id!!,
        name = ingredient.name,
        unit = ingredient.unit,
        amount = ingredient.amount
      )

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

  private fun sumIngredients(category: Map.Entry<OutputCategory, List<OutputIngredient>>): List<OutputIngredient> {
    val ingredientsPerCategory = category.value
    val ingredientsById = ingredientsPerCategory.let { ingredient -> ingredient.groupBy { it.id } }
    val sumAmountByIngredientId =
      ingredientsById.map { (id, ingredients) -> id to ingredients.sumOf { it.amount.toDouble() } }
    return sumAmountByIngredientId.map { (id, amount) ->
      val theIngredient = ingredientsPerCategory.first { it.id == id }
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

  fun deleteAll() {
    shoppingListIngredientRepository.deleteAll()
    shoppingListCategoriesRepository.deleteAll()
    shoppingListRepository.deleteAll()
  }

  private fun menuToShoppingList(menu: OutputMenu): ShoppingList {
    return ShoppingList(
      id = uuidGenerator.generate(),
      firstDay = menu.firstDay
    )
  }

  private fun createShoppingListIngredient(
    shoppingListId: UUID,
    ingredientId: UUID,
    amount: Float
  ): ShoppingListIngredient {
    val newIngredient = ingredientRepository.findById(ingredientId).get()
    val cat = shoppingListCategoriesRepository.findByCategoryId(newIngredient.categoryId)
    val shoppingListCategory: ShoppingListCategory = if (cat.isPresent) {
      cat.get()
    } else {
      val category = categoryRepository.findById(newIngredient.categoryId).get()
      val newCategory = ShoppingListCategory(
        id = uuidGenerator.generate(),
        categoryId = newIngredient.categoryId,
        shoppingListId = shoppingListId,
        name = category.name,
        shopOrder = category.shopOrder
      )
      shoppingListCategoriesRepository.save(newCategory)
    }
    val shoppingListIngredient = ShoppingListIngredient(
      id = newIngredient.id!!,
      name = newIngredient.name,
      ingredientId = ingredientId,
      shoppingListCategoryId = shoppingListCategory.id!!,
      unit = newIngredient.unit,
      amount = amount
    )
    return shoppingListIngredientRepository.save(shoppingListIngredient)
  }

  private fun updateAmount(amount: Float, shoppingListIngredient: ShoppingListIngredient) {
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
  }
}
