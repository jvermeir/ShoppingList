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
import java.util.*

@WebMvcTest(
  value = [CategoryResource::class, CategoryService::class, ConverterResource::class, RecipeResource::class, RecipeService::class, IngredientResource::class, IngredientService::class, RecipeIngredientResource::class, RecipeIngredientService::class]
)
class ConverterTest {
  @Autowired
  lateinit var mockMvc: MockMvc

  @MockkBean
  lateinit var categoryRepository: CategoryRepository

  @MockkBean
  lateinit var recipeRepository: RecipeRepository

  @MockkBean
  lateinit var ingredientRepository: IngredientRepository

  @MockkBean
  lateinit var recipeIngredientRepository: RecipeIngredientRepository

  private val inputCategory1 = Category(name = "cat1", shopOrder = 1)
  private val inputCategory2 = Category(name = "cat2", shopOrder = 2)
  private val inputCategory3 = Category(name = "cat3", shopOrder = 3)
  private val category1 =
    Category(id = UUID.randomUUID(), name = inputCategory1.name, shopOrder = inputCategory1.shopOrder)
  private val category2 =
    Category(id = UUID.randomUUID(), name = inputCategory2.name, shopOrder = inputCategory2.shopOrder)
  private val category3 =
    Category(id = UUID.randomUUID(), name = inputCategory3.name, shopOrder = inputCategory3.shopOrder)
  private val inputRecipe1 = Recipe(name = "recipe1", favorite = false)
  private val recipe1 = Recipe(id = UUID.randomUUID(), name = inputRecipe1.name, favorite = inputRecipe1.favorite)
  private val inputIngredient1 = Ingredient(name = "ingredient1", categoryId = category1.id!!, unit = "N/A")
  private val ingredient1 =
    Ingredient(id = UUID.randomUUID(), name = inputIngredient1.name, categoryId = category1.id!!, unit = "N/A")
  private val inputRecipeIngredient1 =
    RecipeIngredient(recipeId = recipe1.id!!, ingredientId = ingredient1.id!!, amount = 1.0f, unit = "kg")
  private val recipeIngredient1 = RecipeIngredient(
    id = UUID.randomUUID(),
    recipeId = inputRecipeIngredient1.recipeId,
    ingredientId = inputRecipeIngredient1.ingredientId,
    amount = 1.0f, unit = "kg"
  )

  @Test
  fun `categories can be loaded from a data file`() {
    every { categoryRepository.save(inputCategory1) } returns category1
    every { categoryRepository.save(inputCategory2) } returns category2
    every { categoryRepository.save(inputCategory3) } returns category3

    mockMvc.perform(
      post("/converters/categories/testCategoryDatabase.csv")
        .contentType(MediaType.APPLICATION_JSON)
    ).andExpect(status().isOk)
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(jsonPath("$.count").value(3))
  }

  @Test
  fun `recipies can be loaded from a data file`() {
    every { categoryRepository.findByName(inputCategory1.name) } returns Optional.of(category1)
    every { ingredientRepository.findByName(ingredient1.name) } returns Optional.of(ingredient1)
    every { recipeRepository.save(inputRecipe1) } returns recipe1
    every { ingredientRepository.save(inputIngredient1) } returns ingredient1
    every { recipeIngredientRepository.save(inputRecipeIngredient1) } returns recipeIngredient1

    mockMvc.perform(
      post("/converters/cookbook/cookbook.csv")
        .contentType(MediaType.APPLICATION_JSON)
    ).andExpect(status().isOk)
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(jsonPath("$.count").value(1))
  }
}
