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
import kotlinx.datetime.LocalDate
import kotlinx.datetime.toJavaLocalDate
import kotlinx.datetime.toLocalDate

@kotlinx.serialization.Serializable
data class Menu(@Id val id: String? = null, val firstDay: LocalDate)

@WebMvcTest(value = [MenuResource::class, MenuService::class])
class MenuTest {
  @Autowired
  lateinit var mockMvc: MockMvc

  @MockkBean
  lateinit var menuRepository: MenuRepository

  private val march10th = "2022-03-10".toLocalDate()

  private val menu1 = Menu(id = "1", firstDay = march10th)

  @Test
  fun `a menu without id and all properties set is saved correctly and can be loaded`() {
    every { menuRepository.save(menu1) } returns menu1

    mockMvc.perform(
      post("/menu").content(
        Json.encodeToString(menu1)
      ).contentType(MediaType.APPLICATION_JSON)
    ).andExpect(status().isCreated)
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(jsonPath("$.id").value(menu1.id))
      .andExpect(jsonPath("$.firstDay").value(menu1.firstDay.toString()))
  }

  @Test
  fun `GET menu should return 404 when menu not found by id`() {
    every { menuRepository.findById("1") } returns Optional.empty()

    mockMvc.perform(
      get("/menu/1")
    ).andExpect(status().isNotFound)
  }

  @Test
  fun `a list of menus should be returned`() {
    every { menuRepository.findAll() } returns listOf(menu1)

    mockMvc.perform(
      get("/menus").contentType(MediaType.APPLICATION_JSON)
    ).andExpect(status().isOk)
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(jsonPath("$.[0].id").value(menu1.id))
      .andExpect(jsonPath("$.[0].firstDay").value(menu1.firstDay.toString()))
  }

  @Test
  fun `a menu should be returned by findById`() {
    every { menuRepository.findById("1") } returns Optional.of(menu1)

    mockMvc.perform(
      get("/menu/1")
    ).andExpect(status().isOk)
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(jsonPath("$.id").value(menu1.id))
      .andExpect(jsonPath("$.firstDay").value(menu1.firstDay.toString()))
  }

  @Test
  fun `a menu should be returned by findByFirstDay`() {
    every { menuRepository.findByFirstDay(march10th.toJavaLocalDate()) } returns Optional.of(menu1)

    mockMvc.perform(
      get("/menu").param("firstDay", march10th.toString())
    ).andExpect(status().isOk)
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(jsonPath("$.id").value(menu1.id))
      .andExpect(jsonPath("$.firstDay").value(menu1.firstDay.toString()))
  }
}
