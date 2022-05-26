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

data class RecipeIngredients(val recipe: Recipe, val ingredients: List<Ingredient>);

@RestController
class RecipeIngredientResource(val recipeIngredientService: RecipeIngredientService) {
  @GetMapping("/recipe-ingredient/{id}")
  fun findById(@PathVariable(name = "id") id: String) = ResponseEntity.ok(recipeIngredientService.findById(id))

  @GetMapping("/recipe-ingredients")
  fun findByRecipeId(@RequestParam(name = "recipeId") recipeId:String) =
    ResponseEntity.ok(recipeIngredientService.findByRecipeId(recipeId))

  @PostMapping("/recipe-ingredient")
  fun post(@RequestBody recipeIngredient: RecipeIngredient) = ResponseEntity(recipeIngredientService.save(recipeIngredient),  HttpStatus.CREATED)
}

@Service
class RecipeIngredientService(val db: RecipeIngredientRepository) {
  fun findRecipeIngredients(): List<RecipeIngredient> = db.findAll().toList()

  fun findById(id: String):RecipeIngredient = db.findById(id).orElseThrow { ResourceNotFoundException("RecipeIngredient '${id}' not found") }

  fun findByRecipeId(id: String):List<RecipeIngredient> = db.findByRecipeId(id)

  fun save(recipeIngredient: RecipeIngredient) = db.save(recipeIngredient)

  fun deleteAll() = db.deleteAll()
}

@Table("RECIPE_INGREDIENTS")
data class RecipeIngredient(@Id val id: String?, val recipeId: String, val ingredientId: String)

interface RecipeIngredientRepository : CrudRepository<RecipeIngredient, String> {
  @Query("SELECT * FROM recipe_ingredients WHERE recipe_id = :recipeId")
  fun findByRecipeId(recipeId: String): List<RecipeIngredient>
}
