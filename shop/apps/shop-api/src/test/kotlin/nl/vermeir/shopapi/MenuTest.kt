package nl.vermeir.shopapi

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.jdbc.Sql
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.transaction.annotation.Transactional
import java.util.*

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Sql("classpath:/sql/schema.sql")
class MenuTest {
  @Autowired
  lateinit var mockMvc: MockMvc

  @Autowired
  lateinit var menuRepository: MenuRepository

  @Autowired
  lateinit var menuItemRepository: MenuItemRepository

  @Autowired
  lateinit var categoryRepository: CategoryRepository

  @Autowired
  lateinit var ingredientRepository: IngredientRepository

  @Autowired
  lateinit var recipeRepository: RecipeRepository

  @Autowired
  lateinit var recipeIngredientRepository: RecipeIngredientRepository

  @BeforeEach
  fun setup() {
    val cat1 = categoryRepository.save(Category(name = "cat1", shopOrder = 1))
    objectMap["cat1"] = cat1
    val ing1 = ingredientRepository.save(Ingredient(name = "ing1", categoryId = cat1.id!!, unit = "kg"))
    objectMap["ing1"] = ing1
    val recipe1 = recipeRepository.save(Recipe(id = UUID.randomUUID(), name = "recipe1", favorite = false))
    objectMap["recipe1"] = recipe1
    val recipeIngredient1 = recipeIngredientRepository.save(
      RecipeIngredient(
        recipeId = recipe1.id!!,
        ingredientId = ing1.id!!,
        amount = 1.0f, unit = "kg"
      )
    )
    objectMap["recipeIngredient1"] = recipeIngredient1
    val menu1 = menuRepository.save(Menu(id = UUID.randomUUID(), firstDay = march10th))
    objectMap["menu1"] = menu1
    val menuItem1 =
      menuItemRepository.save(
        MenuItem(
          id = UUID.randomUUID(),
          menuId = menu1.id ?: UUID.randomUUID(),
          recipeId = recipe1.id ?: UUID.randomUUID(),
          theDay = march10th
        )
      )
    objectMap["menuItem1"] = menuItem1
  }

  @Test
  fun `a menu without id and all properties set is saved correctly and can be loaded`() {
    val menu = getFromMap<Menu>("menu1").copy(firstDay = march11th)
    val savedMenu = mockMvc.perform(
      post("/menu").content(
        Json.encodeToString(menu)
      ).contentType(MediaType.APPLICATION_JSON)
    ).andExpect(status().isCreated)
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(jsonPath("$.id").value(menu.id.toString()))
      .andExpect(jsonPath("$.firstDay").value(menu.firstDay.toString()))
      .andReturn().response.contentAsString
    val outputMenu = Json.decodeFromString(Menu.serializer(), savedMenu)

    mockMvc.perform(
      get("/menu/${outputMenu.id}")
    ).andExpect(status().isOk)
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(jsonPath("$.id").value(outputMenu.id.toString()))
      .andExpect(jsonPath("$.firstDay").value(outputMenu.firstDay.toString()))
  }

  @Test
  fun `GET menu should return 404 when menu not found by id`() {
    mockMvc.perform(
      get("/menu/${unknownId}")
    ).andExpect(status().isNotFound)
  }

  @Test
  fun `a list of menus should be returned`() {
    val menu1 = getFromMap<Menu>("menu1")
    mockMvc.perform(
      get("/menus").contentType(MediaType.APPLICATION_JSON)
    ).andExpect(status().isOk)
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(jsonPath("$.[0].id").value(menu1.id.toString()))
      .andExpect(jsonPath("$.[0].firstDay").value(menu1.firstDay.toString()))
  }

  @Test
  fun `a menu should be returned by findById`() {
    val menu1 = getFromMap<Menu>("menu1")
    mockMvc.perform(
      get("/menu/${menu1.id}")
    ).andExpect(status().isOk)
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(jsonPath("$.id").value(menu1.id.toString()))
      .andExpect(jsonPath("$.firstDay").value(menu1.firstDay.toString()))
  }

  @Test
  fun `a menu should be returned by findByFirstDay`() {
    val menu1 = getFromMap<Menu>("menu1")
    mockMvc.perform(
      get("/menu/firstDay/${march10th}")
    ).andExpect(status().isOk)
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(jsonPath("$.id").value(menu1.id.toString()))
      .andExpect(jsonPath("$.firstDay").value(menu1.firstDay.toString()))
  }

  @Test
  fun `a menu and its details should be returned by get menu details by firstDay`() {
    val d = march10th.toString()
    val menu1 = getFromMap<Menu>("menu1")
    val menuItem1 = getFromMap<MenuItem>("menuItem1")
    val recipe1 = getFromMap<Recipe>("recipe1")
    val ing1 = getFromMap<Ingredient>("ing1")
    val cat1 = getFromMap<Category>("cat1")

    mockMvc.perform(
      get("/menu/details/firstDay/${d}")
    ).andExpect(status().isOk)
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(content().string("""{"id":"${menu1.id}","firstDay":"2022-03-10","menuItems":[{"id":"${menuItem1.id}","theDay":"2022-03-10","recipe":{"id":"${recipe1.id}","name":"recipe1","favorite":false,"ingredients":[{"id":"${ing1.id}","name":"ing1","category":{"id":"${cat1.id}","name":"cat1","shopOrder":1},"unit":"kg","amount":1.0}]}}]}"""))
  }
}
