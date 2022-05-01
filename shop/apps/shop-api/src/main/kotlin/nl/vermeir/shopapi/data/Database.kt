package nl.vermeir.shopapi.data

import org.springframework.data.annotation.Id
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.relational.core.mapping.Table
import org.springframework.data.repository.CrudRepository

@Table("CATEGORIES")
data class Category(@Id val id: String?, val name: String)
interface CategoryRepository : CrudRepository<Category, String> {}

@Table("INGREDIENTS")
data class Ingredient(@Id val id: String?, val name: String, val categoryId: String)
interface IngredientRepository : CrudRepository<Ingredient, String> {}

@Table("RECIPES")
data class Recipe(@Id val id: String?, val name: String)
interface RecipeRepository : CrudRepository<Recipe, String> {}

data class RI(val recipeName: String, val ingredientName: String);
@Table("RECIPE_INGREDIENTS")
data class RecipeIngredient(@Id val id: String?, val recipeId: String, val ingredientId: String)
interface RecipeIngredientRepository : CrudRepository<RecipeIngredient, String> {
  @Query(value = "SELECT r.name as recipe_name, i.name as ingredient_name FROM recipes r, ingredients i, recipe_ingredients ri WHERE ri.recipe_id = r.id AND ri.ingredient_id = i.id")
  open fun findAllRecipeIngredients(): List<RI>
}

