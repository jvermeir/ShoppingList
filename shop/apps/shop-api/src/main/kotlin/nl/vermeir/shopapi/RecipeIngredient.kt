package nl.vermeir.shopapi

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import kotlinx.serialization.Serializable
import org.springframework.data.jpa.repository.Query
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

  @GetMapping("/recipeIngredients/byRecipeId/{recipeId}")
  fun findRecipeIngredientsByRecipeId(@PathVariable(name = "recipeId") recipeId: UUID) =
    ResponseEntity.ok(recipeIngredientService.findRecipeIngredientsByRecipeId(recipeId))

  @PostMapping("/recipeIngredient")
  fun post(@RequestBody recipeIngredient: RecipeIngredient): ResponseEntity<RecipeIngredient> {
    if (recipeIngredient.unit == null) {
      val ingredient = ingredientService.findById(recipeIngredient.ingredientId)
      recipeIngredient.unit = ingredient.unit
    }
    return ResponseEntity(recipeIngredientService.save(recipeIngredient), HttpStatus.CREATED)
  }

  @PutMapping("/recipeIngredient")
  fun put(@RequestBody recipeIngredient: RecipeIngredient): ResponseEntity<RecipeIngredient> {
    if (recipeIngredient.unit == null) {
      val ingredient = ingredientService.findById(recipeIngredient.ingredientId)
      recipeIngredient.unit = ingredient.unit
    }
    return ResponseEntity(recipeIngredientService.save(recipeIngredient), HttpStatus.OK)
  }

  @DeleteMapping("/recipeIngredient/{id}")
  fun delete(@PathVariable(name = "id") id: UUID) = ResponseEntity.ok(recipeIngredientService.delete(id))

}

@Service
class RecipeIngredientService(
  val db: RecipeIngredientRepository,
  val recipeDetailsDb: RecipeIngredientWithDetailsRepository
) {
  fun list(): List<RecipeIngredient> = db.findAll().toList()

  fun findById(id: UUID): RecipeIngredient =
    db.findById(id).orElseThrow { ResourceNotFoundException("RecipeIngredient '${id}' not found") }

  fun findByRecipeId(id: UUID): List<RecipeIngredient> = db.findByRecipeId(id)

  fun save(recipeIngredient: RecipeIngredient) = db.save(recipeIngredient)

  fun deleteAll() = db.deleteAll()
  fun findRecipeIngredientsByRecipeId(recipeId: UUID): List<RecipeIngredientWithDetails> {
    return recipeDetailsDb.allWithCategoryName(recipeId)
  }

  fun delete(id: UUID) = db.deleteById(id)
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

@Entity(name = "RECIPE_INGREDIENTS_WITH_DETAILS")
data class RecipeIngredientWithDetails(
  @Serializable(with = UUIDSerializer::class)
  @Id val id: UUID,
  @Serializable(with = UUIDSerializer::class)
  val recipeId: UUID,
  @Serializable(with = UUIDSerializer::class)
  val ingredientId: UUID,
  val name: String,
  val unit: String,
  val amount: Float,
)

interface RecipeIngredientRepository : CrudRepository<RecipeIngredient, UUID> {
  fun findByRecipeId(recipeId: UUID): List<RecipeIngredient>
}

interface RecipeIngredientWithDetailsRepository : CrudRepository<RecipeIngredientWithDetails, UUID> {
  @Query("SELECT NEW nl.vermeir.shopapi.RecipeIngredientWithDetails (ri.id, ri.recipeId, ri.ingredientId, i.name, i.unit, ri.amount) FROM INGREDIENTS i, RECIPE_INGREDIENTS ri WHERE i.id = ri.ingredientId AND ri.recipeId = :recipeId")
  fun allWithCategoryName(recipeId: UUID): List<RecipeIngredientWithDetails>
}
