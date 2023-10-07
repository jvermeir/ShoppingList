package nl.vermeir.shopapi

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.annotation.Id
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.relational.core.mapping.Table
import org.springframework.data.repository.CrudRepository
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.*
import java.util.*

// TODO: handle foreign key constraints when deleting categories

@RestController
class CategoryResource {
  @Autowired
  lateinit var categoryService: CategoryService

  @GetMapping("/categories")
  fun list(): List<Category> = categoryService.find()

  @GetMapping("/category/{id}")
  fun findById(@PathVariable(name = "id") id: String) = ResponseEntity.ok(categoryService.findById(id))

  @DeleteMapping("/category/{id}")
  fun delete(@PathVariable(name = "id") id: String) = ResponseEntity.ok(categoryService.delete(id))

  @GetMapping("/category")
  fun findByName(@RequestParam(name = "name") name: String) =
    ResponseEntity.ok(categoryService.findByName(name))

  @PostMapping("/category")
  fun post(@RequestBody category: Category) = ResponseEntity(categoryService.save(category), HttpStatus.CREATED)

  @PutMapping("/category")
  fun put(@RequestBody category: Category) = ResponseEntity(categoryService.save(category), HttpStatus.CREATED)
}

@Service
class CategoryService(val db: CategoryRepository) {
  fun find(): List<Category> = db.findAll().toList()

  fun findById(id: String): Category =
    db.findById(id).orElseThrow { ResourceNotFoundException("Category '${id}' not found") }

  fun findByName(name: String): Category =
    db.findByName(name).orElseThrow { ResourceNotFoundException("Category '${name}' not found") }

  fun save(category: Category): Category = db.save(category)

  fun deleteAll() = db.deleteAll()

  fun delete(id: String) = db.deleteById(id)
}

@Table("CATEGORIES")
data class Category(@Id val id: String? = null, val name: String, val shopOrder: Int)

interface CategoryRepository : CrudRepository<Category, String> {
  @Query("SELECT * FROM categories WHERE name = :name")
  fun findByName(name: String): Optional<Category>
}
