package nl.vermeir.shopapi

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import kotlinx.serialization.Serializable
import org.springframework.data.repository.CrudRepository
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
class RecipeIngredientResource(
  val recipeIngredientService: RecipeIngredientService,
  val ingredientService: IngredientService
) {
  @GetMapping("/recipeIngredients")
  fun list() = recipeIngredientService.list()

  @GetMapping("/recipeIngredient/{id}")
  fun findById(@PathVariable(name = "id") id: UUID) = ResponseEntity.ok(recipeIngredientService.findById(id))

  @GetMapping("/recipeIngredient")
  fun findByRecipeId(@RequestParam(name = "recipeId") recipeId: UUID) =
    ResponseEntity.ok(recipeIngredientService.findByRecipeId(recipeId))

  @PostMapping("/recipeIngredient")
  fun post(@RequestBody recipeIngredient: RecipeIngredient): ResponseEntity<RecipeIngredient> {
    if (recipeIngredient.unit == null) {
      val ingredient = ingredientService.findById(recipeIngredient.ingredientId)
      recipeIngredient.unit = ingredient.unit
    }
    return ResponseEntity(recipeIngredientService.save(recipeIngredient), HttpStatus.CREATED)
  }
}

@Service
class RecipeIngredientService(val db: RecipeIngredientRepository) {
  fun list(): List<RecipeIngredient> = db.findAll().toList()

  fun findById(id: UUID): RecipeIngredient =
    db.findById(id).orElseThrow { ResourceNotFoundException("RecipeIngredient '${id}' not found") }

  fun findByRecipeId(id: UUID): List<RecipeIngredient> = db.findByRecipeId(id)

  fun save(recipeIngredient: RecipeIngredient) = db.save(recipeIngredient)

  fun deleteAll() = db.deleteAll()
}

@Entity(name = "RECIPE_INGREDIENTS")
@Serializable
data class RecipeIngredient(
  @Serializable(with = UUIDSerializer::class)
  @jakarta.persistence.Id @GeneratedValue
  var id: UUID? = null,
  @Serializable(with = UUIDSerializer::class)
  var recipeId: UUID,
  @Serializable(with = UUIDSerializer::class)
  var ingredientId: UUID,
  var amount: Float?,
  var unit: String?
)

interface RecipeIngredientRepository : CrudRepository<RecipeIngredient, UUID> {
  fun findByRecipeId(recipeId: UUID): List<RecipeIngredient>
}
