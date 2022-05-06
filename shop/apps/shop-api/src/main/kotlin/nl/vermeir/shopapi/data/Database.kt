package nl.vermeir.shopapi.data

import org.springframework.data.annotation.Id
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.relational.core.mapping.Table
import org.springframework.data.repository.CrudRepository

@Table("CATEGORIES")
data class Category(@Id val id: String?, val name: String, val shopOrder: Int)
interface CategoryRepository : CrudRepository<Category, String> {
  @Query("SELECT * FROM categories WHERE name = :name")
  fun findByName(name: String):Category?
}

@Table("INGREDIENTS")
data class Ingredient(@Id val id: String?, val name: String, val categoryId: String)
data class IngredientDetails(val ingredientId: String, val ingredientName:String, val categoryId: String, val categoryName:String)
interface IngredientRepository : CrudRepository<Ingredient, String> {
  @Query(
    value = "SELECT i.name as ingredient_name, i.id as ingredient_id, c.name as category_name, c.id as category_id  FROM ingredients i, recipe_ingredients ri, categories c where ri.recipe_id = :recipeId and ri.ingredient_id = i.id and i.category_id = c.id"
  )
  fun ingredientsByRecipe(recipeId:String):List<IngredientDetails>
}

@Table("RECIPES")
data class Recipe(@Id val id: String?, val name: String, val favorite: Boolean)
interface RecipeRepository : CrudRepository<Recipe, String> {
  @Query(value = "SELECT * FROM recipes WHERE name = :name")
  fun findByName(name: String): Recipe;
}

data class RecipeDetails(val recipe: Recipe, val ingredients: List<IngredientDetails>);
data class RI (val recipeName: String, val ingredientName:String)

@Table("RECIPE_INGREDIENTS")
data class RecipeIngredient(@Id val id: String?, val recipeId: String, val ingredientId: String)
interface RecipeIngredientRepository : CrudRepository<RecipeIngredient, String> {
  @Query(value = "SELECT r.name as recipe_name, i.name as ingredient_name FROM recipes r, ingredients i, recipe_ingredients ri WHERE ri.recipe_id = r.id AND ri.ingredient_id = i.id")
  fun findAllRecipeIngredients(): List<RI>
}

