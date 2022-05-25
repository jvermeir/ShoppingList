package nl.vermeir.shopapi

import kotlinx.serialization.Serializable
import org.springframework.data.annotation.Id
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.relational.core.mapping.Table
import org.springframework.data.repository.CrudRepository
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.*
import java.util.NoSuchElementException
import java.util.function.Supplier

data class RecipeDetails(val recipe: Recipe, val ingredients: List<IngredientDetails>);

@RestController
class RecipeIngredientResource(val recipeIngredientService: RecipeIngredientService) {
  @GetMapping("/recipe-ingredients")
  fun index(): List<RecipeIngredient> = recipeIngredientService.findRecipeIngredients()

  @PostMapping("/recipe-ingredient")
  fun post(@RequestBody recipeIngredient: RecipeIngredient) = ResponseEntity(recipeIngredientService.post(recipeIngredient),  HttpStatus.CREATED)
}

@Service
class RecipeIngredientService(val db: RecipeIngredientRepository) {
  fun findRecipeIngredients(): List<RecipeIngredient> = db.findAll().toList()

  fun post(recipeIngredient: RecipeIngredient) = db.save(recipeIngredient)

  fun deleteAll() = db.deleteAll()
}

@Serializable
@Table("RECIPE_INGREDIENTS")
data class RecipeIngredient(@Id val id: String?, val recipeId: String, val ingredientId: String)

interface RecipeIngredientRepository : CrudRepository<RecipeIngredient, String> {
}
