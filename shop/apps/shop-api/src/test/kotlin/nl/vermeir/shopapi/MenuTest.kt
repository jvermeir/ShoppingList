package nl.vermeir.shopapi

import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import kotlinx.datetime.LocalDate
import kotlinx.datetime.toJavaLocalDate
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
    MenuService::class, MenuItemService::class, RecipeService::class, IngredientService::class, CategoryService::class,
    MenuRepository::class, CategoryRepository::class, IngredientRepository::class, MenuItemRepository::class, RecipeRepository::class
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

  private val march10th = LocalDate.parse("2022-03-10")

  private val category1 = Category(id = "1", name = "cat1", shopOrder = 1)
  private val menu1 = Menu(id = "1", firstDay = march10th)
  private val recipe1 = Recipe(id = "1", name = "r1", favorite = true)
  private val menuItem1 = MenuItem(id = "1", menuId = menu1.id!!, recipeId = recipe1.id!!, theDay = march10th)
  private val ingredient1 = Ingredient(id = "1", name = "ing1", categoryId = category1.id!!)
  private val recipeIngredient1 = RecipeIngredientDetails(
    ingredientName = ingredient1.name,
    ingredientId = ingredient1.id!!,
    categoryName = category1.name
  )

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
    every { menuItemRepository.findByMenuId(menuItem1.id.orEmpty()) } returns listOf(menuItem1)

    mockMvc.perform(
      get("/menu/firstDay/${march10th}")
    ).andExpect(status().isOk)
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(jsonPath("$[0].id").value(menuItem1.id))
      .andExpect(jsonPath("$[0].menuId").value(menuItem1.menuId))
      .andExpect(jsonPath("$[0].theDay").value(menuItem1.theDay.toString()))
      .andExpect(jsonPath("$[0].recipeId").value(menuItem1.recipeId))
  }

//  @Test
//  fun `TODO a menu and all details should be returned by `() {
//    every { menuRepository.findByFirstDay(march10th.toJavaLocalDate()) } returns Optional.of(menu1)
//    every { menuItemRepository.findByMenuId(menuItem1.id.orEmpty()) } returns listOf(menuItem1)
//    every { recipeRepository.findById(menuItem1.recipeId) } returns Optional.of(recipe1)
//    every { recipeRepository.listRecipeIngredients(recipe1.name) } returns listOf(recipeIngredient1)
//    every { categoryRepository.findByName(category1.name) } returns Optional.of(category1)
//
//    mockMvc.perform(
//      get("/menu/details/firstDay/${march10th}")
//    ).andExpect(status().isOk)
//      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//      .andExpect(jsonPath("$.id").value(menu1.id))
//      .andExpect(jsonPath("$.firstDay").value(menu1.firstDay.toString()))
//  }
}

