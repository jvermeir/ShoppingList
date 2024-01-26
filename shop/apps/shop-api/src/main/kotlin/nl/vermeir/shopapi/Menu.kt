package nl.vermeir.shopapi

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import kotlinx.serialization.Serializable
import nl.vermeir.shopapi.outputmodel.OutputMenu
import nl.vermeir.shopapi.outputmodel.OutputMenuItem
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
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
  fun findById(@PathVariable(name = "id") id: UUID) = ResponseEntity.ok(menuService.findById(id))

  @GetMapping("/menu/firstDay/{firstDay}")
  fun findByFirstDay(@PathVariable(name = "firstDay") firstDay: LocalDate) =
    ResponseEntity.ok(menuService.findByFirstDay(firstDay))

  @GetMapping("/menu/details/firstDay/{firstDay}")
  fun getMenuDetailsByFirstDay(@PathVariable(name = "firstDay") firstDay: LocalDate) =
    ResponseEntity.ok(menuService.menuDetailsByFirstDay(firstDay))

  @PostMapping("/menu")
  fun post(@RequestBody menu: Menu): ResponseEntity<Menu> {
    return ResponseEntity(menuService.save(menu), HttpStatus.CREATED)
  }

  @PutMapping("/menu")
  fun put(@RequestBody menu: Menu): ResponseEntity<Menu> {
    return ResponseEntity(menuService.save(menu), HttpStatus.OK)
  }

  @DeleteMapping("/menu/{id}")
  fun delete(@PathVariable(name = "id") id: UUID) = ResponseEntity.ok(menuService.delete(id))
}

@Service
class MenuService(
  val menuItemService: MenuItemService,
  val recipeService: RecipeService,
  val menuRepository: MenuRepository,
) {
  fun find(): List<Menu> {
    return menuRepository.findAll().toList()
  }

  fun findById(id: UUID): Menu =
    menuRepository.findById(id).orElseThrow { ResourceNotFoundException("Menu '${id}' not found") }

  fun findByFirstDay(firstDay: LocalDate): Menu {
    val menu = menuRepository.findByFirstDay(firstDay)
    return Menu(menu?.id, menu?.firstDay ?: throw ResourceNotFoundException("Menu for '${firstDay}' not found"))
  }

  fun menuDetailsByFirstDay(firstDay: LocalDate): OutputMenu {
    val menu = findByFirstDay(firstDay)
    val menuItems = menuItemService.findByMenuId(menu.id ?: throw ResourceNotFoundException("Menu '${menu}' not found"))

    val outputMenuItems = menuItems.map { menuItem ->
      val recipe = recipeService.findById(menuItem.recipeId)
      val outputRecipe = recipeService.toOutputRecipe(recipe)
      OutputMenuItem(menuItem.id.toString(), menuItem.theDay, outputRecipe)
    }
    return OutputMenu(menu.id.toString(), firstDay, outputMenuItems)
  }

  fun save(menu: Menu): Menu = menuRepository.save(menu)

  fun deleteAll() = menuRepository.deleteAll()
  fun delete(id: UUID) = menuRepository.deleteById(id)
}

@Entity(name = "MENUS")
@Serializable
data class Menu(
  @jakarta.persistence.Id @GeneratedValue
  @Serializable(with = UUIDSerializer::class)
  var id: UUID? = null,
  @Serializable(with = LocalDateSerializer::class)
  var firstDay: LocalDate
)

interface MenuRepository : CrudRepository<Menu, UUID> {
  fun findByFirstDay(@Param("firstDay") firstDay: LocalDate): Menu?
}
