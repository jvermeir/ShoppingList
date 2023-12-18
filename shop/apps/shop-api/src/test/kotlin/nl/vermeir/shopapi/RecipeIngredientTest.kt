package nl.vermeir.shopapi

import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import java.util.*

@WebMvcTest(value = [RecipeIngredientResource::class, RecipeIngredientService::class])
class RecipeIngredientTest {
  @Autowired
  lateinit var mockMvc: MockMvc

  @MockkBean
  lateinit var recipeIngredientRepository: RecipeIngredientRepository

  @MockkBean
  lateinit var ingredientService: IngredientService

  private val inputRecipeIngredient1 =
    RecipeIngredient(recipeId = UUID.randomUUID(), ingredientId = UUID.randomUUID(), amount = 1.0f, unit = "kg")
  private val recipeIngredient1 = RecipeIngredient(
    id = UUID.randomUUID(),
    recipeId = inputRecipeIngredient1.recipeId,
    ingredientId = inputRecipeIngredient1.ingredientId,
    amount = inputRecipeIngredient1.amount,
    unit = inputRecipeIngredient1.unit
  )

  @Test
  fun `a recipe-ingredient without id but all properties set is saved correctly and can be loaded`() {
    every { recipeIngredientRepository.save(inputRecipeIngredient1) } returns recipeIngredient1

    mockMvc.perform(
      post("/recipeIngredient").content(
        Json.encodeToString(inputRecipeIngredient1)
      ).contentType(MediaType.APPLICATION_JSON)
    ).andExpect(status().isCreated)
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(jsonPath("$.id").value(recipeIngredient1.id.toString()))
      .andExpect(jsonPath("$.recipeId").value(recipeIngredient1.recipeId.toString()))
      .andExpect(jsonPath("$.ingredientId").value(recipeIngredient1.ingredientId.toString()))
      .andExpect(jsonPath("$.amount").value(recipeIngredient1.amount))
      .andExpect(jsonPath("$.unit").value(recipeIngredient1.unit))
  }

  @Test
  fun `GET recipe-ingredient should return 404 when recipe not found by id`() {
    every { recipeIngredientRepository.findById(recipeIngredient1.id!!) } returns Optional.empty()

    mockMvc.perform(
      get("/recipeIngredient/${recipeIngredient1.id}")
    ).andExpect(status().isNotFound)
  }

  @Test
  fun `a list of recipe-ingredients should be returned`() {
    every { recipeIngredientRepository.findAll() } returns listOf(recipeIngredient1)

    mockMvc.perform(
      get("/recipeIngredients").contentType(MediaType.APPLICATION_JSON)
    ).andExpect(status().isOk)
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(jsonPath("$.[0].id").value(recipeIngredient1.id.toString()))
      .andExpect(jsonPath("$.[0].recipeId").value(recipeIngredient1.recipeId.toString()))
      .andExpect(jsonPath("$.[0].ingredientId").value(recipeIngredient1.ingredientId.toString()))
      .andExpect(jsonPath("$.[0].amount").value(recipeIngredient1.amount))
      .andExpect(jsonPath("$.[0].unit").value(recipeIngredient1.unit))
  }

  @Test
  fun `a recipe should be returned by findById`() {
    every { recipeIngredientRepository.findById(recipeIngredient1.id!!) } returns Optional.of(recipeIngredient1)

    mockMvc.perform(
      get("/recipeIngredient/${recipeIngredient1.id}")
    ).andExpect(status().isOk)
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(jsonPath("$.id").value(recipeIngredient1.id.toString()))
      .andExpect(jsonPath("$.recipeId").value(recipeIngredient1.recipeId.toString()))
      .andExpect(jsonPath("$.ingredientId").value(recipeIngredient1.ingredientId.toString()))
      .andExpect(jsonPath("$.amount").value(recipeIngredient1.amount))
      .andExpect(jsonPath("$.unit").value(recipeIngredient1.unit))
  }

  @Test
  fun `a recipe-ingredient should be returned by findByRecipeId`() {
    every { recipeIngredientRepository.findByRecipeId(recipeIngredient1.recipeId) } returns listOf(recipeIngredient1)

    mockMvc.perform(
      get("/recipeIngredient").param("recipeId", recipeIngredient1.recipeId.toString())
    ).andExpect(status().isOk)
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(jsonPath("$.[0].id").value(recipeIngredient1.id.toString()))
      .andExpect(jsonPath("$.[0].recipeId").value(recipeIngredient1.recipeId.toString()))
      .andExpect(jsonPath("$.[0].ingredientId").value(recipeIngredient1.ingredientId.toString()))
      .andExpect(jsonPath("$.[0].amount").value(recipeIngredient1.amount))
      .andExpect(jsonPath("$.[0].unit").value(recipeIngredient1.unit))
  }

  @Test
  fun `if unit is not present, it should be copied from the ingredient`() {
    every { ingredientService.findById(recipeIngredient1.ingredientId) } returns ingredient1
    every { recipeIngredientRepository.save(recipeIngredient1) } returns recipeIngredient1

    mockMvc.perform(
      post("/recipeIngredient").content(
        Json.encodeToString(recipeIngredient1.copy(unit = null))
      ).contentType(MediaType.APPLICATION_JSON)
    ).andExpect(status().isCreated)
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(jsonPath("$.id").value(recipeIngredient1.id.toString()))
      .andExpect(jsonPath("$.recipeId").value(recipeIngredient1.recipeId.toString()))
      .andExpect(jsonPath("$.ingredientId").value(recipeIngredient1.ingredientId.toString()))
      .andExpect(jsonPath("$.amount").value(recipeIngredient1.amount))
      .andExpect(jsonPath("$.unit").value(ingredient1.unit))

  }
}
