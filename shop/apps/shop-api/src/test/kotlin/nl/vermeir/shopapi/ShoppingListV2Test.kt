package nl.vermeir.shopapi

import kotlinx.serialization.json.Json
import nl.vermeir.shopapi.data.OutputShoppingList
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
class ShoppingListV2Test {
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

  @Autowired
  lateinit var shoppingListRepository: ShoppingListRepository

  @Autowired
  lateinit var shoppingListCategoriesRepository: ShoppingListCategoriesRepository

  @Autowired
  lateinit var shoppingListIngredientRepository: ShoppingListIngredientRepository

  private val objectMap = mutableMapOf<String, Any>()

  fun <T> getFromMap(key: String): T {
    return objectMap.get(key)!! as T
  }

  @BeforeEach
  fun setup() {
    val cat1 = categoryRepository.save(Category(name = "cat1", shopOrder = 1))
    objectMap.put("cat1", cat1)
    val cat2 = categoryRepository.save(Category(name = "cat2", shopOrder = 2))
    objectMap.put("cat2", cat2)

    val ing1 = ingredientRepository.save(Ingredient(name = "ing1", categoryId = cat1.id!!))
    objectMap.put("ing1", ing1)
    val ing2 = ingredientRepository.save(Ingredient(name = "ing2", categoryId = cat2.id!!))
    objectMap.put("ing2", ing2)
    val ing3 = ingredientRepository.save(Ingredient(name = "ing3", categoryId = cat2.id!!))
    objectMap.put("ing3", ing3)

    val recipe1 = recipeRepository.save(Recipe(name = "recipe1", favorite = true))
    objectMap.put("recipe1", recipe1)
    val recipe2 = recipeRepository.save(Recipe(name = "recipe2", favorite = true))
    objectMap.put("recipe2", recipe2)

    val recipeIngredient1 = recipeIngredientRepository.save(
      RecipeIngredient(
        recipeId = recipe1.id!!,
        ingredientId = ing1.id!!,
        amount = 1f,
        unit = "kg"
      )
    )
    objectMap.put("recipeIngredient1", recipeIngredient1)
    val recipeIngredient2 = recipeIngredientRepository.save(
      RecipeIngredient(
        recipeId = recipe2.id!!,
        ingredientId = ing2.id!!,
        amount = 2f,
        unit = "kg"
      )
    )
    objectMap.put("recipeIngredient2", recipeIngredient2)
    val recipeIngredient3 = recipeIngredientRepository.save(
      RecipeIngredient(
        recipeId = recipe1.id!!,
        ingredientId = ing2.id!!,
        amount = 3f,
        unit = "kg"
      )
    )
    objectMap.put("recipeIngredient3", recipeIngredient3)

    val menu = menuRepository.save(Menu(firstDay = march10th))
    objectMap.put("menu", menu)

    val menuItem1 = menuItemRepository.save(MenuItem(menuId = menu.id!!, recipeId = recipe1.id!!, theDay = march10th))
    objectMap.put("menuItem1", menuItem1)
    val menuItem2 = menuItemRepository.save(MenuItem(menuId = menu.id!!, recipeId = recipe2.id!!, theDay = march11th))
    objectMap.put("menuItem2", menuItem2)
  }

  // TODO: these test are brittle because of the ordering of results
  @Test
  fun `a shoppinglist should be returned for a menu`() {
    val menu = getFromMap<Menu>("menu")
    val cat1 = getFromMap<Category>("cat1")
    val cat2 = getFromMap<Category>("cat2")

    mockMvc.perform(
      MockMvcRequestBuilders.post("/shoppinglist/frommenu/firstDay/$march10th")
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
      MockMvcRequestBuilders.post("/shoppinglist/frommenu/firstDay/$march10th")
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
      MockMvcRequestBuilders.post("/shoppinglist/frommenu/firstDay/$march10th")
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
      MockMvcRequestBuilders.post("/shoppinglist/frommenu/firstDay/$march10th")
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
      MockMvcRequestBuilders.post("/shoppinglist/frommenu/firstDay/$march10th")
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

/*

done - initial shoppinglist is based on a menu
done - extra ingredients may be added to the shoppinglist
get unit from ingredient when adding an ingredient to the list
done - ingredients of the menu may be removed from the shoppinglist

done - add unit and number to ingredient
done - add numbers if ingredient is the same
add date to perishable ingredient
 */
