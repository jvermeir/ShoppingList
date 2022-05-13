package nl.vermeir.shopapi

import org.springframework.data.annotation.Id
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.relational.core.mapping.Table
import org.springframework.data.repository.CrudRepository
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.*
import java.util.*
import kotlinx.serialization.Serializable

@RestController
class CategoryResource(val categoryService: CategoryService) {
  // TODO: stream output
  @GetMapping("/categories")
  fun index(): List<Category> = categoryService.findCategories()

  @PostMapping("/category")
  fun post(@RequestBody category: Category) = ResponseEntity(categoryService.post(category), HttpStatus.CREATED)

  @GetMapping("/category")
  fun getCategoryByName(@RequestParam(name = "name") name:String) = ResponseEntity.ok(categoryService.getCategoryByName(name));

  @GetMapping("/category/{id}")
  fun getCategoryById(@PathVariable(name = "id") id:String) = ResponseEntity.ok(categoryService.findById(id).get());
}

@Service
class CategoryService(val db: CategoryRepository) {

  fun findCategories(): List<Category> = db.findAll().toList()

  fun post(category: Category): Category {
    if (category.id != null) {
      return db.save(category)
    } else {
      val cat = db.findByName(category.name)
      if (cat!=null) {
        return db.save(cat.copy(shopOrder=category.shopOrder))
      }
      return db.save(category)
    }
  }

  fun getCategoryByName(name: String):Category = db.findByName(name) ?: throw ResourceNotFoundException("Category '${name}' not found")

  fun findById(id: String): Optional<Category> = db.findById(id)

  fun deleteAll() = db.deleteAll()
}

@Serializable
@Table("CATEGORIES")
data class Category(@Id val id: String?, val name: String, val shopOrder: Int)

interface CategoryRepository : CrudRepository<Category, String> {
  @Query("SELECT * FROM categories WHERE name = :name")
  fun findByName(name: String):Category?
}
