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
import java.util.function.Supplier

@Serializable
data class RecipeDetails(val recipe: Recipe, val ingredients: List<IngredientDetails>);

@RestController
class RecipeResource(val recipeService: RecipeService) {
  @GetMapping("/recipes")
  fun index(): List<Recipe> = recipeService.findRecipes()

  @GetMapping("/recipe")
  fun getById(@RequestParam("id") id: String) = recipeService.findById(id)

  @PostMapping("/recipe")
  fun post(@RequestBody recipe: Recipe) = ResponseEntity(recipeService.post(recipe), HttpStatus.CREATED)

  @GetMapping("/recipe-details")
  fun getRecipeWithDetails(@RequestParam("id") id: String):RecipeDetails = recipeService.getRecipeWithDetails(recipeService.findById(id))

  @PostMapping("/recipe-details")
  fun postRecipeWithDetails(@RequestBody recipeDetails: RecipeDetails) = ResponseEntity(recipeService.post(recipeDetails), HttpStatus.CREATED)
}

@Service
class RecipeService(val db: RecipeRepository, val ingredientDb: IngredientRepository, val recipeIngedientDb: RecipeIngredientRepository, val categoryDb: CategoryRepository) {
  fun findRecipes(): List<Recipe> = db.findAll().toList()

  fun getRecipeWithDetails(recipe:Recipe): RecipeDetails {
    val ingredients = ingredientDb.ingredientsByRecipe(recipe.id.orEmpty())
    return RecipeDetails(recipe, ingredients)
  }

  fun post(recipe: Recipe)= save(recipe)

  fun post(recipe: RecipeDetails): RecipeDetails {
    val newRecipe = save(recipe.recipe)
    recipe.ingredients.forEach {

      categoryDb
        .findById(it.categoryId)
        .orElseThrow(Supplier { throw ResourceNotFoundException("category with id ${it.categoryId} or name ${it.categoryName} not found") })

      val ingredient = ingredientDb.save(ingredientFrom(it))
      recipeIngedientDb.save(RecipeIngredient(it.recipeIngredientId, newRecipe.id.orEmpty(), ingredient.id.orEmpty()))
    }
    return getRecipeWithDetails(newRecipe)
  }

  fun ingredientFrom(detail: IngredientDetails): Ingredient = Ingredient(detail.ingredientId, detail.ingredientName, detail.categoryId)

  fun findById(id:String): Recipe = db.findById(id).get()

  fun deleteAll() = db.deleteAll()

  fun save(recipe: Recipe): Recipe {
    if (recipe.id != null) {
      return db.save(recipe)
    } else {
      val rec = db.findByName(recipe.name)
      if (rec!=null) {
        return db.save(rec.copy(favorite = recipe.favorite))
      }
      return db.save(recipe)
    }
  }
}

@Serializable
@Table("RECIPES")
data class Recipe(@Id val id: String?, val name: String, val favorite: Boolean)
interface RecipeRepository : CrudRepository<Recipe, String> {
  @Query(value = "SELECT * FROM recipes WHERE name = :name")
  fun findByName(name: String): Recipe?;
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

  fun post(recipeIngredient: RecipeIngredient) = db.save(recipeIngredient)

  fun deleteAll() = db.deleteAll()
}

@Serializable
@Table("RECIPE_INGREDIENTS")
data class RecipeIngredient(@Id val id: String?, val recipeId: String, val ingredientId: String)

interface RecipeIngredientRepository : CrudRepository<RecipeIngredient, String> {
}
