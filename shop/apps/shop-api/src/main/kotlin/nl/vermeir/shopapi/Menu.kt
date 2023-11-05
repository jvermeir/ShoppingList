package nl.vermeir.shopapi

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import nl.vermeir.shopapi.data.*
import org.springframework.data.annotation.Id
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.relational.core.mapping.Table
import org.springframework.data.repository.CrudRepository
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.*
import kotlinx.datetime.LocalDate
import java.util.*
import javax.persistence.Column

@RestController
class MenuResource(val menuService: MenuService) {
  @GetMapping("/menus")
  fun list(): List<Menu> = menuService.find()

  @GetMapping("/menu/{id}")
  fun findById(@PathVariable(name = "id") id: String) = ResponseEntity.ok(menuService.findById(id))

  @GetMapping("/menu/firstDay/{firstDay}")
  fun findByFirstDay(@PathVariable(name = "firstDay") firstDay: LocalDate) =
    ResponseEntity.ok(menuService.findByFirstDay(firstDay))

  @GetMapping("/menu/details/firstDay/{firstDay}")
  fun getMenuDetailsByFirstDay(@PathVariable(name = "firstDay") firstDay: LocalDate) =
    ResponseEntity.ok(menuService.menuDetailsByFirstDay(firstDay))

  @PostMapping("/menu")
  fun post(@RequestBody menu: Menu): ResponseEntity<Menu> = ResponseEntity(menuService.save(menu), HttpStatus.CREATED)
}

@Service
class MenuService(
  val menuItemService: MenuItemService,
  val recipeService: RecipeService,
  val categoryService: CategoryService,
  val menuRepository: MenuRepository,
) {
  fun find(): List<Menu> {
    return menuRepository.findAll().toList()
  }

  fun findById(id: String): Menu =
    menuRepository.findById(id).orElseThrow { ResourceNotFoundException("Menu '${id}' not found") }

  fun findByFirstDay(firstDay: LocalDate): Menu =
    menuRepository.findByFirstDay(firstDay)
      .orElseThrow { ResourceNotFoundException("Menu for '${firstDay}' not found") }

  fun menuDetailsByFirstDay(firstDay: LocalDate): OutputMenu {
    val menu = findByFirstDay(firstDay)
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

      OutputMenuItem(menuItem.id, menuItem.theDay, outputRecipe)
    }

    return OutputMenu(menu.id, firstDay, outputMenuItems)
  }

  fun save(menu: Menu): Menu = menuRepository.save(menu)

  fun deleteAll() = menuRepository.deleteAll()

  fun convertJavaUtilLocalDateToKotlinxLocalDate(javaUtilLocalDate: java.time.LocalDate): kotlinx.datetime.LocalDate {
    return kotlinx.datetime.LocalDate.parse(javaUtilLocalDate.format(yyyyyMMddFormatter))
  }
}

@Table("MENUS")
data class Menu(
  @Id val id: String?,
  @JsonSerialize(using = DateConversions.Serializer::class)
  @JsonDeserialize(using = DateConversions.Deserializer::class)
  @Column(name = "first_day", columnDefinition = "DATE")
  val firstDay: LocalDate
)

interface MenuRepository : CrudRepository<Menu, String> {
  @Query("SELECT * FROM menus WHERE first_day  = :first_day")
  fun findByFirstDay(first_day: LocalDate): Optional<Menu>
}

val yyyyyMMddFormatter = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd")
