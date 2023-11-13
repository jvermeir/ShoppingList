package nl.vermeir.shopapi

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
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
  fun findById(@PathVariable("id") id: UUID) = ResponseEntity.ok(recipeService.findById(id))

  @GetMapping("/recipe")
  fun findByName(@RequestParam(name = "name") name: String) = ResponseEntity.ok(recipeService.findByName(name))

  @PostMapping("/recipe")
  fun post(@RequestBody recipe: Recipe) = ResponseEntity(recipeService.save(recipe), HttpStatus.CREATED)
}

@Service
class RecipeService(val db: RecipeRepository) {
  fun list(): List<Recipe> = db.findAll().toList()

  fun findById(id: UUID): Recipe =
    db.findById(id.toString()).orElseThrow { ResourceNotFoundException("Recipe '${id}' not found") }

  fun findByName(name: String): Recipe =
    db.findByName(name).orElseThrow { ResourceNotFoundException("Recipe '${name}' not found") }

  fun save(recipe: Recipe): Recipe {
    val res = db.save(recipe)
    println("res: ${res}")
    return res
  }

  fun deleteAll() = db.deleteAll()

//  fun getRecipeIngredients(recipeName: String): List<RecipeIngredientDetails> = db.listRecipeIngredients(recipeName)
}

@Entity(name = "RECIPES")
class Recipe(
  @jakarta.persistence.Id @GeneratedValue var id: UUID? = null,
  var name: String,
  var favorite: Boolean
)

interface RecipeRepository : CrudRepository<Recipe, String> {
  fun findByName(name: String): Optional<Recipe>
}
