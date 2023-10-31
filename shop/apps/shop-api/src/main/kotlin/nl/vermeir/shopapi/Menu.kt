package nl.vermeir.shopapi

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import kotlinx.serialization.Serializable
import nl.vermeir.shopapi.data.*
import org.springframework.data.annotation.Id
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.relational.core.mapping.Table
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.*
import java.time.LocalDate
import java.util.*

@RestController
class MenuResource(val menuService: MenuService) {
  @GetMapping("/menus")
  fun list(): List<Menu> = menuService.find()

  @GetMapping("/menu/{id}")
  fun findById(@PathVariable(name = "id") id: String) = ResponseEntity.ok(menuService.findById(id))

  @GetMapping("/menu/firstDay/{firstDay}")
  fun findByFirstDay(@PathVariable(name = "firstDay") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) firstDay: Datum) =
    ResponseEntity.ok(menuService.findByFirstDay(firstDay))

  @GetMapping("/menu/details/firstDay/{firstDay}")
  fun getMenuDetailsByFirstDay(@PathVariable(name = "firstDay")  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) firstDay: Datum) =
    ResponseEntity.ok(menuService.menuDetailsByFirstDay(firstDay))

  @PostMapping("/menu")
  fun post(@RequestBody menu: Menu): ResponseEntity<Menu> = ResponseEntity(menuService.save(menu), HttpStatus.CREATED)
}

@Service
class MenuService(
  val menuItemService: MenuItemService,
  val recipeService: RecipeService,
  val ingredientService: IngredientService,
  val categoryService: CategoryService,
  val categoryRepository: CategoryRepository,
  val menuRepository: MenuRepository,
  val menuItemRepository: MenuRepository,
  val recipeRepository: RecipeRepository
) {
  fun find(): List<Menu> {
    return menuRepository.findAll().toList()
  }

  fun findById(id: String): Menu =
    menuRepository.findById(id).orElseThrow { ResourceNotFoundException("Menu '${id}' not found") }

  fun findByFirstDay(firstDay: Datum ): List<MenuItem> {
    val menu = menuRepository.findByFirstDay(firstDay.date)
      .orElseThrow { ResourceNotFoundException("Menu for '${firstDay.date}' not found") }
    return menuItemService.findByMenuId(menu.id.orEmpty())
  }

  fun menuDetailsByFirstDay(firstDay: Datum): OutputMenu {
    val menu = menuRepository.findByFirstDay(firstDay.date)
      .orElseThrow { ResourceNotFoundException("Menu for '${firstDay}' not found") }
    val menuItems = menuItemService.findByMenuId(menu.id.orEmpty())

    val outputMenuItems = menuItems.map { menuItem ->
      val recipe = recipeService.findById(menuItem.recipeId)

      val ingredients = recipeService.getRecipeIngredients(recipe.name)
      val outputIngredients = ingredients.map { ingredient ->
        val category = categoryService.findByName(ingredient.categoryName)
        OutputIngredient(
          ingredient.ingredientId,
          ingredient.ingredientName,
          OutputCategory(category.id, category.name, category.shopOrder)
        )
      }
      val outputRecipe = OutputRecipe(recipe.id, recipe.name, recipe.favorite, outputIngredients)

      // TODO: fix date format
      OutputMenuItem(menuItem.id, menuItem.theDay.toString(), outputRecipe)
    }

    return OutputMenu(menu.id, menu.firstDay.toString(), outputMenuItems)
  }

  fun save(menu: Menu): Menu = menuRepository.save(menu)

  fun deleteAll() = menuRepository.deleteAll()
}

@Table("MENUS")
data class Menu(
  @Id val id: String?,
  @JsonSerialize(using = DateConversions.Serializer::class)
  @JsonDeserialize(using = DateConversions.Deserializer::class)
  val firstDay: LocalDate
)

interface MenuRepository : CrudRepository<Menu, String> {
  @Query("SELECT * FROM menus WHERE first_day  = :first_day")
  fun findByFirstDay(first_day: LocalDate): Optional<Menu>}
