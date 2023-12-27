// TODO: Use these types to represent data? like OutputMenuItem in menu-items.tsx

export interface OutputMenu {
  id: string;
  firstDay: string;
  menuItems: OutputMenuItem[];
}

export interface OutputMenuItem {
  id: string;
  theDay: string;
  recipe: OutputRecipe;
}

export interface OutputRecipe {
  id: string;
  name: string;
  favorite: Boolean;
  ingredients: OutputIngredient[];
}

export interface OutputIngredient {
  id: string;
  name: string;
  category: OutputCategory;
  unit: string;
  amount: number;
}

export interface OutputCategory {
  id: string;
  name: string;
  shopOrder: number;
}

export interface OutputShoppingList {
  id: string;
  firstDay: string;
  categories: OutputShoppingListCategory[];
}

export interface OutputShoppingListCategory {
  id: string;
  name: string;
  shopOrder: number;
  ingredients: OutputShoppingListIngredient[];
}

export interface OutputShoppingListIngredient {
  id: string;
  name: string;
  amount: number;
  unit: string;
}
