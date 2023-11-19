package nl.vermeir.shopapi

import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import nl.vermeir.shopapi.data.OutputCategory
import nl.vermeir.shopapi.data.OutputIngredient
import nl.vermeir.shopapi.data.OutputRecipe
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import java.util.*

@WebMvcTest(value = [RecipeResource::class, RecipeService::class, IngredientService::class, RecipeIngredientService::class])
class RecipeTest {
  @Autowired
  lateinit var mockMvc: MockMvc

  @Autowired
  lateinit var recipeService: RecipeService

  @Autowired
  lateinit var recipeIngredientService: RecipeIngredientService

  @MockkBean
  lateinit var recipeRepository: RecipeRepository

  @MockkBean
  lateinit var recipeIngredientRepository: RecipeIngredientRepository

  @MockkBean
  lateinit var ingredientService: IngredientService

  private val recipe1 = Recipe(id = UUID.randomUUID(), name = "recipe1", favorite = true)

  @Test
  fun `a recipe without id and all properties set is saved correctly and can be loaded`() {
    every { recipeRepository.save(recipe1) } returns recipe1

    mockMvc.perform(
      post("/recipe").content(
        Json.encodeToString(recipe1)
      ).contentType(MediaType.APPLICATION_JSON)
    ).andExpect(status().isCreated)
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(jsonPath("$.id").value(recipe1.id.toString()))
      .andExpect(jsonPath("$.name").value(recipe1.name))
      .andExpect(jsonPath("$.favorite").value(recipe1.favorite))
  }

  @Test
  fun `GET recipe should return 404 when recipe not found by id`() {
    every { recipeRepository.findById(recipe1.id!!) } returns Optional.empty()

    mockMvc.perform(
      get("/recipe/${recipe1.id}")
    ).andExpect(status().isNotFound)
  }

  @Test
  fun `a list of recipes should be returned`() {
    every { recipeRepository.findAll() } returns listOf(recipe1)

    mockMvc.perform(
      get("/recipes").contentType(MediaType.APPLICATION_JSON)
    ).andExpect(status().isOk)
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(jsonPath("$.[0].id").value(recipe1.id.toString()))
      .andExpect(jsonPath("$.[0].name").value(recipe1.name))
      .andExpect(jsonPath("$.[0].favorite").value(recipe1.favorite))
  }

  @Test
  fun `a recipe should be returned by findById`() {
    every {
      recipeRepository.findById(
        recipe1.id ?: throw ResourceNotFoundException("Recipe '${recipe1}' not found")
      )
    } returns Optional.of(recipe1)

    mockMvc.perform(
      get("/recipe/${recipe1.id}")
    ).andExpect(status().isOk)
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(jsonPath("$.id").value(recipe1.id.toString()))
      .andExpect(jsonPath("$.name").value(recipe1.name))
      .andExpect(jsonPath("$.favorite").value(recipe1.favorite))
  }

  @Test
  fun `a recipe should be returned by findByName`() {
    every { recipeRepository.findByName(recipe1.name) } returns Optional.of(recipe1)

    mockMvc.perform(
      get("/recipe").param("name", recipe1.name)
    ).andExpect(status().isOk)
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(jsonPath("$.id").value(recipe1.id.toString()))
      .andExpect(jsonPath("$.name").value(recipe1.name))
      .andExpect(jsonPath("$.favorite").value(recipe1.favorite))
  }

  @Test
  fun `a recipe object can be transformed to a OutputRecipe object`() {
    val categoryId = UUID.randomUUID()
    val ingredientId = UUID.randomUUID()
    val recipeId = UUID.randomUUID()
    val recipeIngredientId = UUID.randomUUID()

    val category = Category(categoryId, "cat1", 1)
    val ingredient = Ingredient(ingredientId, "ing1", categoryId)
    val recipe = Recipe(recipeId, "rec1", true)
    val recipeIngredient = RecipeIngredient(recipeIngredientId, recipeId, ingredientId)

    val outputIngredient = OutputIngredient(
      ingredientId.toString(),
      "ing1",
      OutputCategory(category.id.toString(), category.name, category.shopOrder)
    )
    every { ingredientService.toOutputIngredient(ingredient) } returns outputIngredient
    every { ingredientService.findById(ingredientId) } returns ingredient
    every { recipeIngredientService.findByRecipeId(recipeId) } returns listOf(recipeIngredient)

    val expectedOutputRecipe = OutputRecipe(recipeId.toString(), "rec1", true, listOf(outputIngredient))

    val outputRecipe = recipeService.toOutputRecipe(recipe)
    assert(outputRecipe == expectedOutputRecipe)

  }

}
