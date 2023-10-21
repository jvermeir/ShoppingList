package nl.vermeir.shopapi

import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.data.annotation.Id
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import java.util.*

@kotlinx.serialization.Serializable
data class Recipe(@Id val id: String? = null, val name: String, val favorite: Boolean)

@WebMvcTest(value = [RecipeResource::class, RecipeService::class])
class RecipeTest {
  @Autowired
  lateinit var mockMvc: MockMvc

  @MockkBean
  lateinit var recipeRepository: RecipeRepository

  private val recipe1 = Recipe(id = "1", name = "recipe1", favorite = true)

  @Test
  fun `a recipe without id and all properties set is saved correctly and can be loaded`() {
    every { recipeRepository.save(recipe1) } returns recipe1

    mockMvc.perform(
      post("/recipe").content(
        Json.encodeToString(recipe1)
      ).contentType(MediaType.APPLICATION_JSON)
    ).andExpect(status().isCreated)
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(jsonPath("$.id").value(recipe1.id))
      .andExpect(jsonPath("$.name").value(recipe1.name))
      .andExpect(jsonPath("$.favorite").value(recipe1.favorite))
  }

  @Test
  fun `GET recipe should return 404 when recipe not found by id`() {
    every { recipeRepository.findById("1") } returns Optional.empty()

    mockMvc.perform(
      get("/recipe/1")
    ).andExpect(status().isNotFound)
  }

  @Test
  fun `a list of recipes should be returned`() {
    every { recipeRepository.findAll() } returns listOf(recipe1)

    mockMvc.perform(
      get("/recipes").contentType(MediaType.APPLICATION_JSON)
    ).andExpect(status().isOk)
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(jsonPath("$.[0].id").value(recipe1.id))
      .andExpect(jsonPath("$.[0].name").value(recipe1.name))
      .andExpect(jsonPath("$.[0].favorite").value(recipe1.favorite))
  }

  @Test
  fun `a recipe should be returned by findById`() {
    every { recipeRepository.findById("1") } returns Optional.of(recipe1)

    mockMvc.perform(
      get("/recipe/1")
    ).andExpect(status().isOk)
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(jsonPath("$.id").value(recipe1.id))
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
      .andExpect(jsonPath("$.id").value(recipe1.id))
      .andExpect(jsonPath("$.name").value(recipe1.name))
      .andExpect(jsonPath("$.favorite").value(recipe1.favorite))
  }
}
