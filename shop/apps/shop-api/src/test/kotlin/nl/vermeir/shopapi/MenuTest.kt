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

@WebMvcTest(
  value = [
    MenuResource::class,
    MenuService::class, MenuItemService::class, RecipeService::class, IngredientService::class, CategoryService::class, RecipeIngredientService::class
  ]
)

class MenuTest {
  @Autowired
  lateinit var mockMvc: MockMvc

  @MockkBean
  lateinit var menuRepository: MenuRepository

  @MockkBean
  lateinit var menuItemRepository: MenuItemRepository

  @MockkBean
  lateinit var categoryRepository: CategoryRepository

  @MockkBean
  lateinit var ingredientRepository: IngredientRepository

  @MockkBean
  lateinit var recipeRepository: RecipeRepository

  @MockkBean
  lateinit var recipeIngredientRepository: RecipeIngredientRepository

  @Test
  fun `a menu without id and all properties set is saved correctly and can be loaded`() {
    every { menuRepository.save(inputMenu1) } returns menu1
    mockMvc.perform(
      post("/menu").content(
        Json.encodeToString(inputMenu1)
      ).contentType(MediaType.APPLICATION_JSON)
    ).andExpect(status().isCreated)
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(jsonPath("$.id").value(menu1.id.toString()))
      .andExpect(jsonPath("$.firstDay").value(menu1.firstDay.toString()))
  }

  @Test
  fun `GET menu should return 404 when menu not found by id`() {
    every { menuRepository.findById(menu1.id!!) } returns Optional.empty()

    mockMvc.perform(
      get("/menu/${menu1.id}")
    ).andExpect(status().isNotFound)
  }

  @Test
  fun `a list of menus should be returned`() {
    every { menuRepository.findAll() } returns listOf(menu1)

    mockMvc.perform(
      get("/menus").contentType(MediaType.APPLICATION_JSON)
    ).andExpect(status().isOk)
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(jsonPath("$.[0].id").value(menu1.id.toString()))
      .andExpect(jsonPath("$.[0].firstDay").value(menu1.firstDay.toString()))
  }

  @Test
  fun `a menu should be returned by findById`() {
    every { menuRepository.findById(menu1.id!!) } returns Optional.of(menu1)

    mockMvc.perform(
      get("/menu/${menu1.id}")
    ).andExpect(status().isOk)
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(jsonPath("$.id").value(menu1.id.toString()))
      .andExpect(jsonPath("$.firstDay").value(menu1.firstDay.toString()))
  }

  @Test
  fun `a menu should be returned by findByFirstDay`() {
    every { menuRepository.findByFirstDay(march10th) } returns menu1
    every { menuItemRepository.findByMenuId(menuItem1.id ?: UUID.randomUUID()) } returns listOf(menuItem1)

    mockMvc.perform(
      get("/menu/firstDay/${march10th}")
    ).andExpect(status().isOk)
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(jsonPath("$.id").value(menu1.id.toString()))
      .andExpect(jsonPath("$.firstDay").value(menu1.firstDay.toString()))
  }

  @Test
  fun `a menu and its details should be returned by menu details firstday`() {
    every { categoryRepository.findById(category1.id!!) } returns Optional.of(category1)
    every { ingredientRepository.findById(ingredient1.id!!) } returns Optional.of(ingredient1)
    every { recipeRepository.findById(recipe1.id!!) } returns Optional.of(recipe1)
    every { recipeIngredientRepository.findByRecipeId(recipe1.id!!) } returns listOf(recipeIngredient1)
    every { menuItemRepository.findByMenuId(menu1.id!!) } returns listOf(menuItem1)
    every { menuRepository.findByFirstDay(march10th) } returns menu1

    val d = march10th.toString()
    mockMvc.perform(
      get("/menu/details/firstDay/${d}")
    ).andExpect(status().isOk)
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(content().string("""{"id":"$theId","firstDay":"2022-03-10","menuItems":[{"id":"$theId","theDay":"2022-03-10","recipe":{"id":"$theId","name":"r1","favorite":true,"ingredients":[{"id":"$theId","name":"ing1","category":{"id":"$theId","name":"cat1","shopOrder":1},"unit":"kg","amount":1.0}]}}]}"""))
  }
}
