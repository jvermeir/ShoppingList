export function service(): string {
  return 'service';
}

export interface CategoryData {
  id: string;
  name: string;
  shopOrder: number;
}

export interface EditCategoryRequest {
  id: string;
  name: string;
  shopOrder: number;
}

export interface AddCategoryRequest {
  name: string;
  shopOrder: number;
}

export interface IngredientData {
  id: string;
  name: string;
  categoryId: string;
  categoryName: string;
  unit: string;
}

export interface AddIngredientRequest {
  name: string;
  categoryId: string;
  unit: string;
}

export interface EditIngredientRequest {
  id: string;
  name: string;
  categoryId: string;
  unit: string;
}

export interface AddMenuRequest {
  firstDay: string;
}

export interface EditMenuRequest {
  id: string;
  firstDay: string;
}

export interface AddMenuItemRequest {
  menuId: string;
  recipeId: string;
  theDay: string;
}

export interface EditMenuItemRequest {
  id: string;
  menuId: string;
  recipeId: string;
  theDay: string;
}

export interface AddRecipeRequest {
  name: string;
  favorite: boolean;
}

export interface EditRecipeRequest {
  id: string;
  name: string;
  favorite: boolean;
}

export interface AddRecipeIngredientRequest {
  ingredientId: string;
  recipeId: string;
  amount: number;
  unit: string;
}

export interface EditRecipeIngredientRequest {
  id: string;
  recipeId: string;
  ingredientId: string;
  amount: number;
  unit: string;
}

export interface AddShoppingListIngredientRequest {
  shoppingListId: string;
  ingredientId: string;
  amount: number;
  unit: string;
}

export interface EditShoppingListIngredientRequest {
  shoppingListId: string;
  ingredientId: string;
  amount: number;
}

export const createCategory = (req: AddCategoryRequest) => {
  return fetch('/api/category', {
    method: 'POST',
    headers: {
      'Content-type': 'application/json; charset=UTF-8',
    },
    body: JSON.stringify(req),
  });
};

export function getCategories(
  setCategories: (value: CategoryData[]) => void,
  setLoading?: (value: boolean) => void,
  setError?: (value: boolean) => void
): void {
  if (setLoading) setLoading(true);

  fetch('/api/categories')
    .then((_) => _.json())
    .then((categories) => {
      setCategories(categories);
    })
    .catch(setError)
    .finally(() => {
      if (setLoading) setLoading(false);
    });
}

export const updateCategory = (req: EditCategoryRequest) => {
  return fetch('/api/category', {
    method: 'PUT',
    headers: {
      'Content-type': 'application/json; charset=UTF-8',
    },
    body: JSON.stringify(req),
  });
};

export const createIngredient = (req: AddIngredientRequest) => {
  return fetch('/api/ingredient', {
    method: 'POST',
    headers: {
      'Content-type': 'application/json; charset=UTF-8',
    },
    body: JSON.stringify(req),
  });
};

/*
TODO: should result processing be part of the method to get the data (like in getIngredients)?
or should we just return the data and let the caller handle the result (as in updateIngredient)?
 */
export function getIngredients(
  setIngredients: (value: IngredientData[]) => void,
  setLoading?: (value: boolean) => void,
  setError?: (value: boolean) => void
) {
  if (setLoading) setLoading(true);

  fetch('/api/ingredientsWithDetails')
    .then((_) => _.json())
    .then((ingredients) => {
      setIngredients(ingredients);
    })
    .catch(setError)
    .finally(() => {
      if (setLoading) setLoading(false);
    });
}

export const updateIngredient = (req: EditIngredientRequest) => {
  return fetch('/api/ingredient', {
    method: 'PUT',
    headers: {
      'Content-type': 'application/json; charset=UTF-8',
    },
    body: JSON.stringify(req),
  });
};

export const createMenu = (req: AddMenuRequest) => {
  return fetch('/api/menu', {
    method: 'POST',
    headers: {
      'Content-type': 'application/json; charset=UTF-8',
    },
    body: JSON.stringify(req),
  });
};

export const updateMenu = (req: EditMenuRequest) => {
  return fetch('/api/menu', {
    method: 'PUT',
    headers: {
      'Content-type': 'application/json; charset=UTF-8',
    },
    body: JSON.stringify(req),
  });
};

export const postMenuItem = (req: AddMenuItemRequest) => {
  return fetch('/api/menuItem', {
    method: 'POST',
    headers: {
      'Content-type': 'application/json; charset=UTF-8',
    },
    body: JSON.stringify(req),
  });
};

export const updateMenuItem = (req: EditMenuItemRequest) => {
  return fetch('/api/menuItem', {
    method: 'PUT',
    headers: {
      'Content-type': 'application/json; charset=UTF-8',
    },
    body: JSON.stringify(req),
  });
};

export const createRecipe = (req: AddRecipeRequest) => {
  return fetch('/api/recipe', {
    method: 'POST',
    headers: {
      'Content-type': 'application/json; charset=UTF-8',
    },
    body: JSON.stringify(req),
  });
};

export const updateRecipe = (req: EditRecipeRequest) => {
  return fetch('/api/recipe', {
    method: 'PUT',
    headers: {
      'Content-type': 'application/json; charset=UTF-8',
    },
    body: JSON.stringify(req),
  });
};

export const createRecipeIngredient = (req: AddRecipeIngredientRequest) => {
  return fetch('/api/recipeIngredient', {
    method: 'POST',
    headers: {
      'Content-type': 'application/json; charset=UTF-8',
    },
    body: JSON.stringify(req),
  });
};

export const updateRecipeIngredient = (req: EditRecipeIngredientRequest) => {
  return fetch('/api/recipeIngredient', {
    method: 'PUT',
    headers: {
      'Content-type': 'application/json; charset=UTF-8',
    },
    body: JSON.stringify(req),
  });
};

export const addShoppingListIngredient = (
  req: AddShoppingListIngredientRequest
) => {
  return fetch(
    `api/shoppinglist/${req.shoppingListId}/add/ingredient/${req.ingredientId}/amount/${req.amount}`,
    {
      method: 'POST',
      headers: {
        'Content-type': 'application/json; charset=UTF-8',
      },
      body: JSON.stringify({}),
    }
  );
};

export const updateShoppingListIngredient = (
  req: EditShoppingListIngredientRequest
) => {
  return fetch(
    `api/shoppinglist/${req.shoppingListId}/ingredient/${req.ingredientId}/amount/${req.amount}`,
    {
      method: 'PUT',
      headers: {
        'Content-type': 'application/json; charset=UTF-8',
      },
      body: JSON.stringify({}),
    }
  );
};
