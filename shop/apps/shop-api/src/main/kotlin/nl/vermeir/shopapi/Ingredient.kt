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

/*
[{"id":"fe353fdd-2c18-4d31-9b1b-13c1d45887b1","name":"1300","shopOrder":10},{"id":"8ee4276d-a906-4bc6-af44-8b0997bb2ef2","name":"d","shopOrder":0}]
 */

@RestController
class IngredientResource(val ingredientService: IngredientService) {
  @GetMapping("/ingredients")
  fun list(): List<Ingredient> = ingredientService.list()

  @GetMapping("/ingredients-view")
  fun listIngredientsView(): List<IngredientView> = ingredientService.listIngredientsView()

  @GetMapping("/ingredient/{id}")
  fun findById(@PathVariable(name = "id") id: String) = ResponseEntity.ok(ingredientService.findById(id))

  @DeleteMapping("/ingredient/{id}")
  fun delete(@PathVariable(name = "id") id: String) = ResponseEntity.ok(ingredientService.delete(id))

  @GetMapping("/ingredient")
  fun findByName(@RequestParam(name = "name") name: String) =
    ResponseEntity.ok(ingredientService.findByName(name))

  @PostMapping("/ingredient")
  fun post(@RequestBody ingredient: Ingredient) = ResponseEntity(ingredientService.save(ingredient), HttpStatus.CREATED)
}

inline fun <T> nullable(f: () -> T): T? = try {
  f()
} catch (e: Error) {
  throw e
} catch (e: Throwable) {
  null
}
@Service
class IngredientService(val db: IngredientRepository) {
  fun list(): List<Ingredient> = db.findAll().toList()

  fun listIngredientsView():List<IngredientView> = db.ingredientsView()

  fun findById(id: String): Ingredient = db.findById(id).orElseThrow { ResourceNotFoundException("Ingredient '${id}' not found") }

  fun findByName(name: String): Ingredient? = nullable {
    db.findByName(name).orElseThrow { ResourceNotFoundException("Ingredient '${name}' not found") }
  }

  fun save(ingredient: Ingredient):Ingredient = db.save(ingredient)

  fun deleteAll() = db.deleteAll()

  fun delete(id: String) = db.deleteById(id)
}

@Table("INGREDIENTS")
data class Ingredient(@Id val id: String? = null, val name: String, val categoryId: String)

data class IngredientView(@Id val id: String? = null, val name: String, val categoryId: String?, val categoryName: String?)

interface IngredientRepository : CrudRepository<Ingredient, String> {
  @Query("SELECT * FROM ingredients WHERE name = :name")
  fun findByName(name: String): Optional<Ingredient>

  @Query("SELECT i.id, i.name, i.category_id, c.name as category_name FROM ingredients i left outer join categories c on i.category_id = c.id ")
  fun ingredientsView(): List<IngredientView>
}
