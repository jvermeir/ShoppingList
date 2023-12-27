package nl.vermeir.shopapi

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import kotlinx.serialization.Serializable
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.*
import java.time.LocalDate
import java.util.*

@RestController
class MenuItemResource(val menuItemService: MenuItemService) {
  @GetMapping("/menuItems")
  fun list() = menuItemService.list()

  @GetMapping("/menuItem/{id}")
  fun findById(@PathVariable(name = "id") id: UUID) = ResponseEntity.ok(menuItemService.findById(id))

  @GetMapping("/menuItem")
  fun findByDay(@RequestParam(name = "day") day: LocalDate) = ResponseEntity.ok(menuItemService.findByDay(day))

  @GetMapping("/menuItemsWithDetails/{id}")
  fun findMenuItemsWithDetails(@PathVariable(name = "id") id: UUID) =
    ResponseEntity.ok(menuItemService.findMenuItemsWithDetailsById(id))

  @PostMapping("/menuItem")
  fun post(@RequestBody menuItem: MenuItem) = ResponseEntity(menuItemService.save(menuItem), HttpStatus.CREATED)

  @PutMapping("/menuItem")
  fun put(@RequestBody menuItem: MenuItem) = ResponseEntity(menuItemService.save(menuItem), HttpStatus.OK)

  @DeleteMapping("/menuItem/{id}")
  fun delete(@PathVariable(name = "id") id: UUID) = ResponseEntity.ok(menuItemService.delete(id))
}

@Service
class MenuItemService(val db: MenuItemRepository, val menuItemDetailsRepository: MenuItemWithDetailsRepository) {
  fun list(): List<MenuItem> = db.findAll().toList()

  fun findById(id: UUID): MenuItem =
    db.findById(id).orElseThrow { ResourceNotFoundException("MenuItem '${id}' not found") }

  fun findByMenuId(menuId: UUID): List<MenuItem> = db.findByMenuId(menuId)

  fun findByDay(day: LocalDate): List<MenuItem> = db.findByTheDay(day)

  fun save(menuItem: MenuItem) = db.save(menuItem)

  fun deleteAll() = db.deleteAll()

  fun findMenuItemsWithDetailsById(id: UUID): List<MenuItemWithDetails> {
    return menuItemDetailsRepository.allWithRecipeName(id)
  }

  fun delete(id: UUID) = db.deleteById(id)
}

@Entity(name = "MENU_ITEMS")
@Serializable
data class MenuItem(
  @jakarta.persistence.Id @GeneratedValue
  @Serializable(with = UUIDSerializer::class)
  var id: UUID? = null,
  @Serializable(with = UUIDSerializer::class)
  var menuId: UUID,
  @Serializable(with = UUIDSerializer::class)
  var recipeId: UUID,
  @Serializable(with = LocalDateSerializer::class)
  var theDay: LocalDate
)

@Entity(name = "MENU_ITEMS_WITH_DETAILS")
data class MenuItemWithDetails(
  @Serializable(with = UUIDSerializer::class)
  @Id val id: UUID,
  @Serializable(with = UUIDSerializer::class)
  var menuId: UUID,
  @Serializable(with = UUIDSerializer::class)
  var recipeId: UUID,
  var recipeName: String,
  @Serializable(with = LocalDateSerializer::class)
  var theDay: LocalDate
)

interface MenuItemRepository : CrudRepository<MenuItem, UUID> {
  fun findByTheDay(theDay: LocalDate): List<MenuItem>

  fun findByMenuId(menuId: UUID): List<MenuItem>
}

interface MenuItemWithDetailsRepository : CrudRepository<MenuItemWithDetails, UUID> {
  @Query("SELECT NEW nl.vermeir.shopapi.MenuItemWithDetails (mi.id, mi.menuId, mi.recipeId, r.name, mi.theDay) FROM MENU_ITEMS mi, RECIPES r WHERE mi.recipeId = r.id AND mi.id = :menuItemId")
  fun allWithRecipeName(menuItemId: UUID): List<MenuItemWithDetails>
}
