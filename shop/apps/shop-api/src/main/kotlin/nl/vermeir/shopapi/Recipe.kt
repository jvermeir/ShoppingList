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
class RecipeResource(val recipeService: RecipeService) {
  @GetMapping("/recipes")
  fun list(): List<Recipe> = recipeService.list()

  @GetMapping("/recipe/{id}")
  fun findById(@PathVariable("id") id: String) = ResponseEntity.ok(recipeService.findById(id))

  @GetMapping("/recipe")
  fun findByName(@RequestParam(name="name") name: String) = ResponseEntity.ok(recipeService.findByName(name))

  @PostMapping("/recipe")
  fun post(@RequestBody recipe: Recipe) = ResponseEntity(recipeService.save(recipe), HttpStatus.CREATED)
}

@Service
class RecipeService(val db: RecipeRepository) {
  fun list(): List<Recipe> = db.findAll().toList()

  fun findById(id:String): Recipe = db.findById(id).orElseThrow { ResourceNotFoundException("Recipe '${id}' not found") }

  fun findByName(name: String): Recipe =
    db.findByName(name).orElseThrow { ResourceNotFoundException("Recipe '${name}' not found")}

  fun save(recipe: Recipe): Recipe = db.save(recipe)

  fun deleteAll() = db.deleteAll()
  fun convert() {
    TODO("Not yet implemented")
  }
}

@Table("RECIPES")
data class Recipe(@Id val id: String?, val name: String, val favorite: Boolean)

interface RecipeRepository : CrudRepository<Recipe, String> {
  @Query(value = "SELECT * FROM recipes WHERE name = :name")
  fun findByName(name: String): Optional<Recipe>
}
