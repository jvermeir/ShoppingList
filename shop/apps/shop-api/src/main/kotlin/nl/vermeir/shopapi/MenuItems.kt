package nl.vermeir.shopapi

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.annotation.Id
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.relational.core.mapping.Table
import org.springframework.data.repository.CrudRepository
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.*
import java.time.LocalDate

@RestController
class MenuItemResource(val menuItemService: MenuItemService) {
  @GetMapping("/menu-items")
  fun list() = menuItemService.list()

  @GetMapping("/menu-item/{id}")
  fun findById(@PathVariable(name = "id") id: String) = ResponseEntity.ok(menuItemService.findById(id))

  @GetMapping("/menu-item")
  fun findByDay(@RequestParam(name="day") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) day: LocalDate) = ResponseEntity.ok(menuItemService.findByDay(day))

  @PostMapping("/menu-item")
  fun post(@RequestBody menuItem: MenuItem) = ResponseEntity(menuItemService.save(menuItem), HttpStatus.CREATED)
}

@Service
class MenuItemService(val db: MenuItemRepository) {
  fun list(): List<MenuItem> = db.findAll().toList()

  fun findById(id: String):MenuItem = db.findById(id).orElseThrow { ResourceNotFoundException("MenuItem '${id}' not found") }

  fun findByDay(day: LocalDate):List<MenuItem> = db.findByDay(day)

  fun save(menuItem: MenuItem) = db.save(menuItem)

  fun deleteAll() = db.deleteAll()
}

@Table("MENU_ITEMS")
data class MenuItem(@Id val id: String? = null, val menuId: String, val recipeId: String,
                    @JsonSerialize(using = DateConversions.Serializer::class)
                    @JsonDeserialize(using = DateConversions.Deserializer::class)
                    val theDay: LocalDate)

interface MenuItemRepository : CrudRepository<MenuItem, String> {
  @Query("SELECT * FROM menu_items WHERE the_day = :theDay")
  fun findByDay(theDay: LocalDate): List<MenuItem>
}
