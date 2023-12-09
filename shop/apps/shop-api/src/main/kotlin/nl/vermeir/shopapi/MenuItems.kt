package nl.vermeir.shopapi

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import kotlinx.serialization.Serializable
import org.springframework.data.repository.CrudRepository
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.*
import java.time.LocalDate
import java.util.*

@RestController
class MenuItemResource(val menuItemService: MenuItemService) {
  @GetMapping("/menuitems")
  fun list() = menuItemService.list()

  @GetMapping("/menuitem/{id}")
  fun findById(@PathVariable(name = "id") id: UUID) = ResponseEntity.ok(menuItemService.findById(id))

  @GetMapping("/menuitem")
  fun findByDay(@RequestParam(name = "day") day: LocalDate) = ResponseEntity.ok(menuItemService.findByDay(day))

  @PostMapping("/menuitem")
  fun post(@RequestBody menuItem: MenuItem) = ResponseEntity(menuItemService.save(menuItem), HttpStatus.CREATED)
}

@Service
class MenuItemService(val db: MenuItemRepository) {
  fun list(): List<MenuItem> = db.findAll().toList()

  fun findById(id: UUID): MenuItem =
    db.findById(id).orElseThrow { ResourceNotFoundException("MenuItem '${id}' not found") }

  fun findByMenuId(menuId: UUID): List<MenuItem> = db.findByMenuId(menuId)

  fun findByDay(day: LocalDate): List<MenuItem> = db.findByTheDay(day)

  fun save(menuItem: MenuItem) = db.save(menuItem)

  fun deleteAll() = db.deleteAll()
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

interface MenuItemRepository : CrudRepository<MenuItem, UUID> {
  fun findByTheDay(theDay: LocalDate): List<MenuItem>

  fun findByMenuId(menuId: UUID): List<MenuItem>
}
