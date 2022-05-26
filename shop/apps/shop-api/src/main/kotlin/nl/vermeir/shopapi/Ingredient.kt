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

@RestController
class IngredientResource(val ingredientService: IngredientService) {
  @GetMapping("/ingredients")
  fun list(): List<Ingredient> = ingredientService.list()

  @GetMapping("/ingredient/{id}")
  fun findById(@PathVariable(name = "id") id: String) = ResponseEntity.ok(ingredientService.findById(id))

  @GetMapping("/ingredient")
  fun findByName(@RequestParam(name = "name") name: String) =
    ResponseEntity.ok(ingredientService.findByName(name))

  @PostMapping("/ingredient")
  fun post(@RequestBody ingredient: Ingredient) = ResponseEntity(ingredientService.save(ingredient), HttpStatus.CREATED)
}

@Service
class IngredientService(val db: IngredientRepository) {
  fun list(): List<Ingredient> = db.findAll().toList()

  fun findById(id: String): Ingredient = db.findById(id).orElseThrow { ResourceNotFoundException("Ingredient '${id}' not found") }

  fun findByName(name: String): Ingredient =
    db.findByName(name).orElseThrow { ResourceNotFoundException("Ingredient '${name}' not found") }

  fun save(ingredient: Ingredient):Ingredient = db.save(ingredient)

  fun deleteAll() = db.deleteAll()
}

@Table("INGREDIENTS")
data class Ingredient(@Id val id: String?, val name: String, val categoryId: String)

interface IngredientRepository : CrudRepository<Ingredient, String> {
  @Query("SELECT * FROM ingredients WHERE name = :name")
  fun findByName(name: String): Optional<Ingredient>
}

