package nl.vermeir.shopapi

import nl.vermeir.shopapi.data.OutputShoppingList
import nl.vermeir.shopapi.data.OutputShoppingListCategory
import nl.vermeir.shopapi.data.OutputShoppingListIngredient
import java.time.LocalDate
import java.util.*

val theId = "cb494d70-5c5c-45d4-ac66-9b84fe096fc6"
val anotherId = "6aa26577-43e5-4565-a3fb-01737b63d9cc"
val march10th = LocalDate.parse("2022-03-10")
val march11th = LocalDate.parse("2022-03-11")
val menu1 = Menu(id = UUID.fromString(theId), firstDay = march10th)
val inputMenu1 = Menu(firstDay = march10th)
val category1 =
  Category(id = UUID.fromString(theId), name = "cat1", shopOrder = 1)
val category2 =
  Category(id = UUID.fromString(anotherId), name = "cat2", shopOrder = 2)
val ingredient1 =
  Ingredient(id = UUID.fromString(theId), name = "ing1", categoryId = category1.id!!, unit = "kg")
val ingredient2 =
  Ingredient(id = UUID.fromString(anotherId), name = "ing1", categoryId = category2.id!!, unit = "kg")
val recipe1 =
  Recipe(id = UUID.fromString(theId), name = "r1", favorite = true)
val recipeIngredient1 = RecipeIngredient(
  id = UUID.fromString(theId),
  recipeId = recipe1.id!!,
  ingredientId = ingredient1.id!!,
  amount = 1.0f, unit = "kg"
)
val menuItem1 =
  MenuItem(
    id = UUID.fromString(theId),
    menuId = menu1.id!!,
    recipeId = recipe1.id!!,
    theDay = march10th
  )
val shoppingList1 = ShoppingList(
  id = UUID.fromString(theId),
  firstDay = march10th
)
val shoppingListCategory1 = ShoppingListCategory(
  id = UUID.fromString(theId),
  name = "cat1",
  shopOrder = 1,
  shoppingListId = shoppingList1.id!!,
  categoryId = UUID.fromString(theId)

)
val shoppingListCategory2 = ShoppingListCategory(
  id = UUID.fromString(anotherId),
  name = "cat2",
  shopOrder = 1,
  shoppingListId = shoppingList1.id!!,
  categoryId = UUID.fromString(anotherId)
)

val shoppingListIngredient1 = ShoppingListIngredient(
  id = UUID.fromString(theId),
  name = "ing1",
  ingredientId = UUID.fromString(theId),
  shoppingListCategoryId = UUID.fromString(theId),
  amount = 1.0f,
  unit = "kg"
)
val shoppingListIngredient2 = ShoppingListIngredient(
  id = UUID.fromString(anotherId),
  name = "ing2",
  ingredientId = UUID.fromString(anotherId),
  shoppingListCategoryId = UUID.fromString(anotherId),
  amount = 1.0f,
  unit = "kg"
)

//

val c1Id = UUID.fromString("cb494d70-0001-45d4-ac66-9b84fe096fc6")
val c1 = Category(id = c1Id, name = "cat1", shopOrder = 1)
val c2Id = UUID.fromString("cb494d70-0002-45d4-ac66-9b84fe096fc6")
val c2 = Category(id = c2Id, name = "cat2", shopOrder = 2)

val i1Id = UUID.fromString("cb494d70-0003-45d4-ac66-9b84fe096fc6")
val i1 = Ingredient(id = i1Id, name = "ing1", categoryId = c1.id!!, unit = "kg")
val i2Id = UUID.fromString("cb494d70-0004-45d4-ac66-9b84fe096fc6")
val i2 = Ingredient(id = i2Id, name = "ing2", categoryId = c2.id!!, unit = "kg")

val r1Id = UUID.fromString("cb494d70-0005-45d4-ac66-9b84fe096fc6")
val r1 = Recipe(id = r1Id, name = "r1", favorite = true)
val r2Id = UUID.fromString("cb494d70-0006-45d4-ac66-9b84fe096fc6")
val r2 = Recipe(id = r2Id, name = "r2", favorite = true)

val r1i1Id = UUID.fromString("cb494d70-0007-45d4-ac66-9b84fe096fc6")
val r1i1 = RecipeIngredient(
  id = r1i1Id,
  recipeId = r1.id!!,
  ingredientId = i1.id!!,
  amount = 1.0f, unit = "kg"
)
val r1i2Id = UUID.fromString("cb494d70-0008-45d4-ac66-9b84fe096fc6")
val r1i2 = RecipeIngredient(
  id = r1i2Id,
  recipeId = r1.id!!,
  ingredientId = i2.id!!,
  amount = 2.0f, unit = "kg"
)
val r2i1Id = UUID.fromString("cb494d70-0009-45d4-ac66-9b84fe096fc6")
val r2i1 = RecipeIngredient(
  id = r2i1Id,
  recipeId = r2.id!!,
  ingredientId = i1.id!!,
  amount = 3.0f, unit = "kg"
)

val mId = UUID.fromString("cb494d70-0010-45d4-ac66-9b84fe096fc6")
val m = Menu(id = mId, firstDay = march10th)

val mi1Id = UUID.fromString("cb494d70-0011-45d4-ac66-9b84fe096fc6")
val mi1 = MenuItem(
  id = mi1Id,
  menuId = m.id!!,
  recipeId = r1.id!!,
  theDay = march10th
)
val mi2Id = UUID.fromString("cb494d70-0012-45d4-ac66-9b84fe096fc6")
val mi2 = MenuItem(
  id = mi2Id,
  menuId = m.id!!,
  recipeId = r2.id!!,
  theDay = march11th
)

val sl1Id = UUID.fromString("cb494d70-0013-45d4-ac66-9b84fe096fc6")
val sl1 = ShoppingList(id = sl1Id, firstDay = march10th)
val slc1Id = UUID.fromString("cb494d70-0014-45d4-ac66-9b84fe096fc6")
val slc1 = ShoppingListCategory(
  id = slc1Id,
  name = c1.name,
  shopOrder = 1,
  shoppingListId = sl1.id!!,
  categoryId = c1.id!!
)
val slc2Id = UUID.fromString("cb494d70-0015-45d4-ac66-9b84fe096fc6")

val slc2 = ShoppingListCategory(
  id = slc2Id,
  name = c2.name,
  shopOrder = 2,
  shoppingListId = sl1.id!!,
  categoryId = c2.id!!
)
val sli1Id = UUID.fromString("cb494d70-0016-45d4-ac66-9b84fe096fc6")
val sli1 = ShoppingListIngredient(
  id = UUID.fromString("cb494d70-0017-45d4-ac66-9b84fe096fc6"),
  name = i1.name,
  ingredientId = i1.id!!,
  shoppingListCategoryId = slc1.id!!,
  amount = 1.0f,
  unit = "kg"
)
val sli2Id = UUID.fromString("cb494d70-0018-45d4-ac66-9b84fe096fc6")

val sli2 = ShoppingListIngredient(
  id = UUID.fromString("cb494d70-0019-45d4-ac66-9b84fe096fc6"),
  name = i2.name,
  ingredientId = i2.id!!,
  shoppingListCategoryId = slc2.id!!,
  amount = 2.0f,
  unit = "kg"
)
val osl1 = OutputShoppingList(
  id = sl1Id.toString(),
  firstDay = march10th,
  categories = listOf(
    OutputShoppingListCategory(
      id = slc1Id.toString(),
      name = c1.name,
      shopOrder = 1,
      ingredients = listOf(
        OutputShoppingListIngredient(
          id = sli1Id.toString(),
          name = i1.name,
          amount = 1.0f,
          unit = "kg"
        )
      )
    ),
    OutputShoppingListCategory(
      id = slc2Id.toString(),
      name = c2.name,
      shopOrder = 2,
      ingredients = listOf(
        OutputShoppingListIngredient(
          id = sli2Id.toString(),
          name = i2.name,
          amount = 2.0f,
          unit = "kg"
        )
      )
    )
  )
)


/*
cat1 -- c1Id
cat2 -- c2Id
menu with 2 recipes  -- mId
menuItem1 -- mi1Id
  recipe1 with 2 ingredients -- r1Id
    ingredient1   -- i1Id, r1i1Id -> cat1
    ingredient2  -- i2Id, r1i2Id -> cat2
menuItem2  -- mi2Id
  recipe2 with 1 ingredient -- r2Id
    ingredient1  -- i1Id1, ri3Id

shoppinglist with 2 categories -- theId
  category1 -- theId
    ingredient1 -- theId
    ingredient1 -- theId
  category2 -- anotherId
    ingredient2 -- anotherId
 */
