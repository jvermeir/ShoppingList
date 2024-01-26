package nl.vermeir.shopapi

import kotlinx.serialization.json.Json
import nl.vermeir.shopapi.outputmodel.OutputShoppingList
import org.hamcrest.Matchers
import org.hamcrest.text.MatchesPattern
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.jdbc.Sql
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Sql("classpath:/sql/schema.sql")
class ShoppingListTest {
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

  private val objectMap = mutableMapOf<String, Any>()

  // TOOD: this is a duplicate from the method in Fixtures.kt, using that version causes this test to fail (?)
  fun <T> getFromMap(key: String): T {
    return objectMap[key]!! as T
  }

  @BeforeEach
  fun setup() {
    val cat1 = categoryRepository.save(Category(name = "cat1", shopOrder = 1))
    objectMap["cat1"] = cat1
    val cat2 = categoryRepository.save(Category(name = "cat2", shopOrder = 2))
    objectMap["cat2"] = cat2

    val ing1 = ingredientRepository.save(Ingredient(name = "ing1", categoryId = cat1.id!!, unit = "kg"))
    objectMap["ing1"] = ing1
    val ing2 = ingredientRepository.save(Ingredient(name = "ing2", categoryId = cat2.id!!, unit = "kg"))
    objectMap["ing2"] = ing2
    val ing3 = ingredientRepository.save(Ingredient(name = "ing3", categoryId = cat2.id!!, unit = "kg"))
    objectMap["ing3"] = ing3

    val recipe1 = recipeRepository.save(Recipe(name = "recipe1", favorite = true))
    objectMap["recipe1"] = recipe1
    val recipe2 = recipeRepository.save(Recipe(name = "recipe2", favorite = true))
    objectMap["recipe2"] = recipe2

    val recipeIngredient1 = recipeIngredientRepository.save(
      RecipeIngredient(
        recipeId = recipe1.id!!,
        ingredientId = ing1.id!!,
        amount = 1f,
        unit = "kg"
      )
    )
    objectMap["recipeIngredient1"] = recipeIngredient1
    val recipeIngredient2 = recipeIngredientRepository.save(
      RecipeIngredient(
        recipeId = recipe2.id!!,
        ingredientId = ing2.id!!,
        amount = 2f,
        unit = "kg"
      )
    )
    objectMap["recipeIngredient2"] = recipeIngredient2
    val recipeIngredient3 = recipeIngredientRepository.save(
      RecipeIngredient(
        recipeId = recipe1.id!!,
        ingredientId = ing2.id!!,
        amount = 3f,
        unit = "kg"
      )
    )
    objectMap["recipeIngredient3"] = recipeIngredient3

    val menu = menuRepository.save(Menu(firstDay = march10th))
    objectMap["menu"] = menu

    objectMap["menuItem1"] =
      menuItemRepository.save(MenuItem(menuId = menu.id!!, recipeId = recipe1.id!!, theDay = march10th))
    objectMap["menuItem2"] =
      menuItemRepository.save(MenuItem(menuId = menu.id!!, recipeId = recipe2.id!!, theDay = march11th))
  }

  // TODO: these test are brittle because of the ordering of results
  @Test
  fun `a shoppinglist should be returned for a menu`() {
    val menu = getFromMap<Menu>("menu")
    val cat1 = getFromMap<Category>("cat1")
    val cat2 = getFromMap<Category>("cat2")

    mockMvc.perform(
      MockMvcRequestBuilders.post("/shoppinglist/fromMenu/firstDay/$march10th")
    ).andExpect(MockMvcResultMatchers.status().isOk)
      .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(jsonPath("$.id").value(MatchesPattern.matchesPattern("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}\$")))
      .andExpect(jsonPath("$.firstDay").value(menu.firstDay.toString()))
      .andExpect(
        jsonPath(
          "$.categories.[*].name", Matchers.containsInAnyOrder(
            cat1.name, cat2.name
          )
        )
      )
      .andExpect(jsonPath("$.categories.length()").value(2))
      .andExpect(jsonPath("$.categories[0].ingredients[0].name").value("ing1"))
      .andExpect(jsonPath("$.categories[0].ingredients[0].amount").value(1f))
      .andExpect(jsonPath("$.categories[1].ingredients[0].name").value("ing2"))
      .andExpect(jsonPath("$.categories[1].ingredients[0].amount").value(5f))
  }

  @Test
  fun `a shoppinglist should be returned from the shoppinglist table`() {
    mockMvc.perform(
      MockMvcRequestBuilders.post("/shoppinglist/fromMenu/firstDay/$march10th")
    ).andExpect(MockMvcResultMatchers.status().isOk)
      .andReturn().response.contentAsString

    mockMvc.perform(
      MockMvcRequestBuilders.get("/shoppinglist/firstDay/$march10th")
    ).andExpect(MockMvcResultMatchers.status().isOk)
      .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(jsonPath("$.categories.length()").value(2))
      .andExpect(jsonPath("$.categories[0].ingredients[0].name").value("ing1"))
      .andExpect(jsonPath("$.categories[0].ingredients[0].amount").value(1f))
      .andExpect(jsonPath("$.categories[1].ingredients[0].name").value("ing2"))
      .andExpect(jsonPath("$.categories[1].ingredients[0].amount").value(5f))
  }

  @Test
  fun `the amount of an ingredient can be updated`() {
    val shoppingList = mockMvc.perform(
      MockMvcRequestBuilders.post("/shoppinglist/fromMenu/firstDay/$march10th")
    ).andExpect(MockMvcResultMatchers.status().isOk)
      .andReturn().response.contentAsString

    val outputShoppingList = Json.decodeFromString<OutputShoppingList>(shoppingList)
    val category0 = outputShoppingList.categories[0]
    val ingredient0 = category0.ingredients[0]

    mockMvc.perform(
      MockMvcRequestBuilders.put("/shoppinglist/${outputShoppingList.id}/ingredient/${ingredient0.id}/amount/10")
    ).andExpect(MockMvcResultMatchers.status().isOk)
      .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(jsonPath("$.categories.length()").value(2))
      .andExpect(jsonPath("$.categories[0].ingredients[0].name").value("ing1"))
      .andExpect(jsonPath("$.categories[0].ingredients[0].amount").value(10f))
      .andExpect(jsonPath("$.categories[1].ingredients[0].name").value("ing2"))
      .andExpect(jsonPath("$.categories[1].ingredients[0].amount").value(5f))
  }

  @Test
  fun `an ingredient can be removed from the list`() {
    val shoppingList = mockMvc.perform(
      MockMvcRequestBuilders.post("/shoppinglist/fromMenu/firstDay/$march10th")
    ).andExpect(MockMvcResultMatchers.status().isOk)
      .andReturn().response.contentAsString

    val outputShoppingList = Json.decodeFromString<OutputShoppingList>(shoppingList)
    val category1 = outputShoppingList.categories[1]
    val ingredient1 = category1.ingredients[0]

    mockMvc.perform(
      MockMvcRequestBuilders.put("/shoppinglist/${outputShoppingList.id}/ingredient/${ingredient1.id}/amount/0")
    ).andExpect(MockMvcResultMatchers.status().isOk)
      .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(jsonPath("$.categories[0].ingredients[0].name").value("ing1"))
      .andExpect(jsonPath("$.categories[0].ingredients[0].amount").value(1f))
      .andExpect(jsonPath("$.categories.length()").value(1))
  }

  @Test
  fun `an new ingredient can be added to the shoppinglist`() {
    val shoppingList = mockMvc.perform(
      MockMvcRequestBuilders.post("/shoppinglist/fromMenu/firstDay/$march10th")
    ).andExpect(MockMvcResultMatchers.status().isOk)
      .andReturn().response.contentAsString

    val outputShoppingList = Json.decodeFromString<OutputShoppingList>(shoppingList)
    val ingredient3 = getFromMap<Ingredient>("ing3")

    mockMvc.perform(
      MockMvcRequestBuilders.put("/shoppinglist/${outputShoppingList.id}/ingredient/${ingredient3.id}/amount/3")
    ).andExpect(MockMvcResultMatchers.status().isOk)
      .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(jsonPath("$.categories.length()").value(2))
      .andExpect(jsonPath("$.categories[0].ingredients[0].name").value("ing1"))
      .andExpect(jsonPath("$.categories[0].ingredients[0].amount").value(1f))
      .andExpect(jsonPath("$.categories[1].ingredients[0].name").value("ing2"))
      .andExpect(jsonPath("$.categories[1].ingredients[0].amount").value(5f))
      .andExpect(jsonPath("$.categories[1].ingredients[1].name").value("ing3"))
      .andExpect(jsonPath("$.categories[1].ingredients[1].amount").value(3f))
  }
}
