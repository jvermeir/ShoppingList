package nl.vermeir.shopapi

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.jdbc.Sql
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.transaction.annotation.Transactional


@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Sql("classpath:/sql/schema.sql")
class ConverterTest {
  @Autowired
  lateinit var mockMvc: MockMvc

  @Autowired
  lateinit var categoryRepository: CategoryRepository

  @Test
  fun `categories can be loaded from a data file`() {
    mockMvc.perform(
      post("/converters/categories/testCategoryDatabase.csv")
        .contentType(MediaType.APPLICATION_JSON)
    ).andExpect(status().isOk)
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(jsonPath("$.count").value(3))
  }

  @Test
  fun `recipies can be loaded from a data file`() {
    categoryRepository.save(Category(name = "cat1", shopOrder = 1))
    categoryRepository.save(Category(name = "cat2", shopOrder = 2))
    categoryRepository.save(Category(name = "cat3", shopOrder = 3))

    mockMvc.perform(
      post("/converters/cookbook/cookbook.csv")
        .contentType(MediaType.APPLICATION_JSON)
    ).andExpect(status().isOk)
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(jsonPath("$.count").value(1))
  }
}
