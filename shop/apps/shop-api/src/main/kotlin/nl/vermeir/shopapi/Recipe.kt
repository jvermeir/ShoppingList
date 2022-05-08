package nl.vermeir.shopapi

import org.springframework.data.annotation.Id
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.relational.core.mapping.Table
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.*
import kotlinx.serialization.Serializable
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

@Serializable
data class RecipeDetails(val recipe: Recipe, val ingredients: List<IngredientDetails>);
data class RI (val recipeName: String, val ingredientName:String)

@RestController
class RecipeResource(val recipeService: RecipeService) {
  @GetMapping("/recipes")
  fun index(): List<Recipe> = recipeService.findRecipes()

  @GetMapping("/recipe")
  fun getById(@RequestParam("id") id: String) = recipeService.findById(id)

  @GetMapping("/recipe-details")
  fun getRecipeWithDetails(@RequestParam("name") name: String) = recipeService.findByName(name)

  @PostMapping("/recipe")
  fun post(@RequestBody recipe: Recipe) = ResponseEntity(recipeService.post(recipe),  HttpStatus.CREATED)
}

@Service
class RecipeService(val db: RecipeRepository, val ingredientDb: IngredientRepository) {
  fun findRecipes(): List<Recipe> = db.findAll().toList()

  fun findByName(name:String): RecipeDetails {
    val recipe = db.findByName(name)
    return getRecipeWithDetails(recipe)
  }

  fun getRecipeWithDetails(recipe:Recipe): RecipeDetails {
    val ingredients = recipe.id?.let { ingredientDb.ingredientsByRecipe(it) }
    return RecipeDetails(recipe, ingredients.orEmpty())
  }

  fun post(recipe: Recipe)= db.save(recipe)

  fun findById(id:String): Recipe = db.findById(id).get()

  fun deleteAll() = db.deleteAll()

}

@Serializable
@Table("RECIPES")
data class Recipe(@Id val id: String?, val name: String, val favorite: Boolean)
interface RecipeRepository : CrudRepository<Recipe, String> {
  @Query(value = "SELECT * FROM recipes WHERE name = :name")
  fun findByName(name: String): Recipe;
}

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
  fun findAllRecipeIngredients(): List<RI> = db.findAllRecipeIngredients().toList()

  fun post(recipeIngredient: RecipeIngredient) = db.save(recipeIngredient)

  fun deleteAll() = db.deleteAll()
}

@Serializable
@Table("RECIPE_INGREDIENTS")
data class RecipeIngredient(@Id val id: String?, val recipeId: String, val ingredientId: String)

interface RecipeIngredientRepository : CrudRepository<RecipeIngredient, String> {
  @Query(value = "SELECT r.name as recipe_name, i.name as ingredient_name FROM recipes r, ingredients i, recipe_ingredients ri WHERE ri.recipe_id = r.id AND ri.ingredient_id = i.id")
  fun findAllRecipeIngredients(): List<RI>
}
