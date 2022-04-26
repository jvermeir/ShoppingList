package nl.vermeir.shop

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.annotation.Id
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.relational.core.mapping.Table
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@SpringBootApplication
class ShopApplication

fun main(args: Array<String>) {
  runApplication<ShopApplication>(*args)
}

@RestController
class MenuItemResource(val service: MenuitemService) {
  @GetMapping
  fun index(): List<MenuItem> = service.findMenuItems()

  @PostMapping
  fun post(@RequestBody menuItem: MenuItem) {
    service.post(menuItem)
  }

}

@Service
class MenuitemService(val db: MenuItemRepository) {

  fun findMenuItems(): List<MenuItem> = db.findMenuItems()

  fun post(menuItem: MenuItem) {
    db.save(menuItem)
  }
}

interface MenuItemRepository : CrudRepository<MenuItem, String> {

  @Query("select * from menuitems")
  fun findMenuItems(): List<MenuItem>
}

@Table("MENUITEMS")
data class MenuItem(@Id val id: String?, val item: String)


