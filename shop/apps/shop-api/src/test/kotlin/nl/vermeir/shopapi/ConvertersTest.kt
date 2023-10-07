package nl.vermeir.shopapi

import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@WebMvcTest(value = [CategoryResource::class, CategoryService::class, ConverterResource::class])
class ConverterTest {
  @Autowired
  lateinit var mockMvc: MockMvc

  @MockkBean
  lateinit var categoryRepository: CategoryRepository

  private val category1 = Category( name="cat1", shopOrder = 1)
  private val category2 = Category( name="cat2", shopOrder = 2)
  private val category3 = Category( name="cat3", shopOrder = 3)

  @Test
  fun `categories can be loaded from a data file`() {
    every { categoryRepository.save(category1) } returns category1
    every { categoryRepository.save(category2) } returns category2
    every { categoryRepository.save(category3) } returns category3

    mockMvc.perform(
      post("/converters/testCategoryDatabase.csv")
        .contentType(MediaType.APPLICATION_JSON)
    ).andExpect(status().isOk)
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(jsonPath("$.count").value(3))
  }
}
