package nl.vermeir.shopapi

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import org.springframework.beans.factory.annotation.Autowired
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
  fun findById(@PathVariable(name = "id") id: UUID) = ResponseEntity.ok(categoryService.findById(id))

  @DeleteMapping("/category/{id}")
  fun delete(@PathVariable(name = "id") id: UUID) = ResponseEntity.ok(categoryService.delete(id))

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

  fun findById(id: UUID): Category =
    db.findById(id.toString()).orElseThrow { ResourceNotFoundException("Category '${id}' not found") }

  fun findByName(name: String): Category =
    db.findByName(name).orElseThrow { ResourceNotFoundException("Category '${name}' not found") }

  fun save(category: Category): Category = db.save(category)

  fun deleteAll() = db.deleteAll()

  fun delete(id: UUID) = db.deleteById(id.toString())

//  fun toOutputCategory(category: Category): OutputCategory {
//    return OutputCategory(category.id.orEmpty(), category.name, category.shopOrder)
//  }
}

@Entity(name = "CATEGORIES")
class Category(
  @jakarta.persistence.Id @GeneratedValue var id: UUID? = null,
  var name: String,
  var shopOrder: Int
)

interface CategoryRepository : CrudRepository<Category, String> {
  fun findByName(name: String): Optional<Category>
}
