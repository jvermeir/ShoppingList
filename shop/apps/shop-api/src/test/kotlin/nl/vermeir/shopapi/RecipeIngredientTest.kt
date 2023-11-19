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

  private val inputRecipeIngredient1 =
    RecipeIngredient(recipeId = UUID.randomUUID(), ingredientId = UUID.randomUUID())
  private val recipeIngredient1 = RecipeIngredient(
    id = UUID.randomUUID(),
    inputRecipeIngredient1.recipeId,
    inputRecipeIngredient1.ingredientId
  )

  @Test
  fun `a recipe-ingredient without id and all properties set is saved correctly and can be loaded`() {
    every { recipeIngredientRepository.save(inputRecipeIngredient1) } returns recipeIngredient1

    mockMvc.perform(
      post("/recipe-ingredient").content(
        Json.encodeToString(inputRecipeIngredient1)
      ).contentType(MediaType.APPLICATION_JSON)
    ).andExpect(status().isCreated)
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(jsonPath("$.id").value(recipeIngredient1.id.toString()))
      .andExpect(jsonPath("$.recipeId").value(recipeIngredient1.recipeId.toString()))
      .andExpect(jsonPath("$.ingredientId").value(recipeIngredient1.ingredientId.toString()))
  }

  @Test
  fun `GET recipe-ingredient should return 404 when recipe not found by id`() {
    every { recipeIngredientRepository.findById(recipeIngredient1.id!!) } returns Optional.empty()

    mockMvc.perform(
      get("/recipe-ingredient/${recipeIngredient1.id}")
    ).andExpect(status().isNotFound)
  }

  @Test
  fun `a list of recipe-ingredients should be returned`() {
    every { recipeIngredientRepository.findAll() } returns listOf(recipeIngredient1)

    mockMvc.perform(
      get("/recipe-ingredients").contentType(MediaType.APPLICATION_JSON)
    ).andExpect(status().isOk)
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(jsonPath("$.[0].id").value(recipeIngredient1.id.toString()))
      .andExpect(jsonPath("$.[0].recipeId").value(recipeIngredient1.recipeId.toString()))
      .andExpect(jsonPath("$.[0].ingredientId").value(recipeIngredient1.ingredientId.toString()))
  }

  @Test
  fun `a recipe should be returned by findById`() {
    every { recipeIngredientRepository.findById(recipeIngredient1.id!!) } returns Optional.of(recipeIngredient1)

    mockMvc.perform(
      get("/recipe-ingredient/${recipeIngredient1.id}")
    ).andExpect(status().isOk)
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(jsonPath("$.id").value(recipeIngredient1.id.toString()))
      .andExpect(jsonPath("$.recipeId").value(recipeIngredient1.recipeId.toString()))
      .andExpect(jsonPath("$.ingredientId").value(recipeIngredient1.ingredientId.toString()))
  }

  @Test
  fun `a recipe-ingredient should be returned by findByRecipeId`() {
    every { recipeIngredientRepository.findByRecipeId(recipeIngredient1.recipeId) } returns listOf(recipeIngredient1)

    mockMvc.perform(
      get("/recipe-ingredient").param("recipeId", recipeIngredient1.recipeId.toString())
    ).andExpect(status().isOk)
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(jsonPath("$.[0].id").value(recipeIngredient1.id.toString()))
      .andExpect(jsonPath("$.[0].recipeId").value(recipeIngredient1.recipeId.toString()))
      .andExpect(jsonPath("$.[0].ingredientId").value(recipeIngredient1.ingredientId.toString()))
  }
}
