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
data class Ingredient(@Id val id: String? = null, val name: String, val categoryId: String)

@WebMvcTest(value = [IngredientResource::class, IngredientService::class])
class IngredientTest {
  @Autowired
  lateinit var mockMvc: MockMvc

  @MockkBean
  lateinit var ingredientRepository: IngredientRepository

  private val ingredient1 = Ingredient(id = "1", name = "name1", categoryId = "cat1")

  @Test
  fun `a ingredient without id and all properties set is saved correctly and can be loaded`() {
    every { ingredientRepository.save(ingredient1) } returns ingredient1

    mockMvc.perform(
      post("/ingredient").content(
        Json.encodeToString(ingredient1)
      ).contentType(MediaType.APPLICATION_JSON)
    ).andExpect(status().isCreated)
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(jsonPath("$.name").value(ingredient1.name))
      .andExpect(jsonPath("$.id").value(ingredient1.id))
      .andExpect(jsonPath("$.categoryId").value(ingredient1.categoryId))
  }

  @Test
  fun `GET ingredient should return 404 when ingredient not found by id`() {
    every { ingredientRepository.findById("1") } returns Optional.empty()

    mockMvc.perform(
      get("/ingredient/1")
    ).andExpect(status().isNotFound)
  }

  @Test
  fun `GET ingredient should return 404 when ingredient not found by name`() {
    every { ingredientRepository.findByName("1") } returns Optional.empty()

    mockMvc.perform(
      get("/ingredient").param("name", "1")
    ).andExpect(status().isNotFound)
  }

  @Test
  fun `a list of categories should be returned`() {
    every { ingredientRepository.findAll() } returns listOf(ingredient1)

    mockMvc.perform(
      get("/ingredients").contentType(MediaType.APPLICATION_JSON)
    ).andExpect(status().isOk)
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(jsonPath("$.[0].name").value(ingredient1.name))
      .andExpect(jsonPath("$.[0].id").value(ingredient1.id))
      .andExpect(jsonPath("$.[0].categoryId").value(ingredient1.categoryId))
  }

  @Test
  fun `a ingredient should be returned by findById`() {
    every { ingredientRepository.findById("1") } returns Optional.of(ingredient1)

    mockMvc.perform(
      get("/ingredient/1")
    ).andExpect(status().isOk)
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(jsonPath("$.name").value(ingredient1.name))
      .andExpect(jsonPath("$.id").value(ingredient1.id))
      .andExpect(jsonPath("$.categoryId").value(ingredient1.categoryId))
  }

  @Test
  fun `a ingredient should be returned by getCategoryByName`() {
    every { ingredientRepository.findByName("name1") } returns Optional.of(ingredient1)

    mockMvc.perform(
      get("/ingredient").param("name", "name1")
    ).andExpect(status().isOk)
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(jsonPath("$.name").value(ingredient1.name))
      .andExpect(jsonPath("$.id").value(ingredient1.id))
      .andExpect(jsonPath("$.categoryId").value(ingredient1.categoryId))
  }
}
