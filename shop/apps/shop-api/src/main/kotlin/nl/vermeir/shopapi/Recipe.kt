package nl.vermeir.shopapi

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import kotlinx.serialization.Serializable
import nl.vermeir.shopapi.data.OutputRecipe
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
class RecipeService(
  val recipeRepository: RecipeRepository,
  val recipeIngredientService: RecipeIngredientService,
  val ingredientService: IngredientService
) {
  fun list(): List<Recipe> = recipeRepository.findAll().toList()

  fun findById(id: UUID): Recipe {
    return recipeRepository.findById(id)
      .orElseThrow { ResourceNotFoundException("Recipe '${id}' not found") }
  }

  fun findByName(name: String): Recipe =
    recipeRepository.findByName(name).orElseThrow { ResourceNotFoundException("Recipe '${name}' not found") }

  fun save(recipe: Recipe): Recipe {
    val res = recipeRepository.save(recipe)
    println("res: ${res}")
    return res
  }

  fun deleteAll() = recipeRepository.deleteAll()

  fun toOutputRecipe(recipe: Recipe): OutputRecipe {
    val recipeIngredients = recipeIngredientService.findByRecipeId(recipe.id!!)
    val outputIngredients = recipeIngredients.map {
      ingredientService.toOutputIngredient(ingredientService.findById(it.ingredientId), it.unit ?: "", it.amount ?: 0f)
    }
    return OutputRecipe(recipe.id.toString(), recipe.name, recipe.favorite, outputIngredients)
  }
}

@Entity(name = "RECIPES")
@Serializable
data class Recipe(
  @jakarta.persistence.Id @GeneratedValue
  @Serializable(with = UUIDSerializer::class)
  var id: UUID? = null,
  var name: String,
  var favorite: Boolean
)

interface RecipeRepository : CrudRepository<Recipe, UUID> {
  fun findByName(name: String): Optional<Recipe>
}
