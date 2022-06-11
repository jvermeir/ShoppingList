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
data class Category(@Id val id: String? = null, val name: String, val shopOrder: Int)

@WebMvcTest(value = [CategoryResource::class, CategoryService::class])
class CategoryTest {
  @Autowired
  lateinit var mockMvc: MockMvc

  @MockkBean
  lateinit var categoryRepository: CategoryRepository

  private val category1 = Category("1", "cat1", 1)

  @Test
  fun `a category without id and all properties set is saved correctly and can be loaded`() {
    every { categoryRepository.save(category1) } returns category1

    mockMvc.perform(
      post("/category").content(
        Json.encodeToString(category1)
      ).contentType(MediaType.APPLICATION_JSON)
    ).andExpect(status().isCreated)
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(jsonPath("$.name").value(category1.name))
      .andExpect(jsonPath("$.id").value(category1.id))
      .andExpect(jsonPath("$.shopOrder").value(category1.shopOrder))
  }

  @Test
  fun `GET category should return 404 when category not found by id`() {
    every { categoryRepository.findById("1") } returns Optional.empty()

    mockMvc.perform(
      get("/category/1")
    ).andExpect(status().isNotFound)
  }

  @Test
  fun `GET category should return 404 when category not found by name`() {
    every { categoryRepository.findByName("1") } returns Optional.empty()

    mockMvc.perform(
      get("/category").param("name", "1")
    ).andExpect(status().isNotFound)
  }

  @Test
  fun `a list of categories should be returned`() {
    every { categoryRepository.findAll() } returns listOf(category1)

    mockMvc.perform(
      get("/categories").contentType(MediaType.APPLICATION_JSON)
    ).andExpect(status().isOk)
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(jsonPath("$.[0].name").value(category1.name))
      .andExpect(jsonPath("$.[0].id").value(category1.id))
      .andExpect(jsonPath("$.[0].shopOrder").value(category1.shopOrder))
  }

  @Test
  fun `a category should be returned by findById`() {
    every { categoryRepository.findById("1") } returns Optional.of(category1)

    mockMvc.perform(
      get("/category/1")
    ).andExpect(status().isOk)
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(jsonPath("$.name").value(category1.name))
      .andExpect(jsonPath("$.id").value(category1.id))
      .andExpect(jsonPath("$.shopOrder").value(category1.shopOrder))
  }

  @Test
  fun `a category should be returned by getCategoryByName`() {
    every { categoryRepository.findByName("cat1") } returns Optional.of(category1)

    mockMvc.perform(
      get("/category").param("name", "cat1")
    ).andExpect(status().isOk)
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(jsonPath("$.name").value(category1.name))
      .andExpect(jsonPath("$.id").value(category1.id))
      .andExpect(jsonPath("$.shopOrder").value(category1.shopOrder))
  }
}
