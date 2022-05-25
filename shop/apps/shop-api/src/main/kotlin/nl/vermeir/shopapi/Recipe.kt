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
import java.util.function.Supplier

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

  // TODO: do we need these?
  @GetMapping("/recipe-details")
  fun getRecipeWithDetails(@RequestParam("id") id: String):RecipeDetails = recipeService.getRecipeWithDetails(recipeService.findById(id))

  @PostMapping("/recipe-details")
  fun postRecipeWithDetails(@RequestBody recipeDetails: RecipeDetails) = ResponseEntity(recipeService.post(recipeDetails), HttpStatus.CREATED)
}

@Service
class RecipeService(val db: RecipeRepository, val ingredientDb: IngredientRepository, val recipeIngedientDb: RecipeIngredientRepository, val categoryDb: CategoryRepository) {
  fun list(): List<Recipe> = db.findAll().toList()

  fun findById(id:String): Recipe = db.findById(id).orElseThrow { ResourceNotFoundException("Recipe '${id}' not found") }

  fun findByName(name: String): Recipe =
    db.findByName(name).orElseThrow { ResourceNotFoundException("Recipe '${name}' not found")}

  fun save(recipe: Recipe): Recipe = db.save(recipe)

  fun deleteAll() = db.deleteAll()

  // TODO: do we need these?
  fun getRecipeWithDetails(recipe:Recipe): RecipeDetails {
    val ingredients = ingredientDb.ingredientsByRecipe(recipe.id.orEmpty())
    return RecipeDetails(recipe, ingredients)
  }

  fun post(recipeDetails: RecipeDetails): RecipeDetails {
    val newRecipe = save(recipeDetails.recipe)
    recipeDetails.ingredients.forEach {
      categoryDb
        .findById(it.categoryId)
        .orElseThrow(Supplier { throw ResourceNotFoundException("category with id ${it.categoryId} not found") })

      val ingredient = ingredientDb.save(ingredientFrom(it))
      recipeIngedientDb.save(RecipeIngredient(it.recipeIngredientId, newRecipe.id.orEmpty(), ingredient.id.orEmpty()))
    }
    return getRecipeWithDetails(newRecipe)
  }

  fun ingredientFrom(detail: IngredientDetails): Ingredient = Ingredient(detail.ingredientId, detail.ingredientName, detail.categoryId)
}

@Table("RECIPES")
data class Recipe(@Id val id: String?, val name: String, val favorite: Boolean)

interface RecipeRepository : CrudRepository<Recipe, String> {
  @Query(value = "SELECT * FROM recipes WHERE name = :name")
  fun findByName(name: String): Optional<Recipe>
}
