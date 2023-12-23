import {
  Container,
  Paper,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Typography,
} from '@mui/material';

import { Loading } from '../components/loading/loading';
import React, { useEffect, useState } from 'react';

import fetch from 'cross-fetch';
import { Navigation } from '../components/navigation/navigation';
import { AddRecipe } from '../components/recipe/add-recipe';
import { Recipe } from '../components/recipe/recipe';
import { IngredientData } from './ingredients';

/*
recipes: shows the list of recipes, this page also loads the list of ingredients because we need those in detail pages where recipes are added or changed
  based on the recipes table,
  has add button
 */

export interface RecipeData {
  id: string;
  name: string;
  favorite: boolean;
}

export const RecipesPage = () => {
  const [recipes, setRecipes] = useState<RecipeData[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<boolean>(false);
  const [ingredients, setIngredients] = useState<IngredientData[]>([]);

  function getRecipes() {
    setLoading(true);

    fetch('/api/recipes')
      .then((_) => _.json())
      .then((recipes) => {
        setRecipes(recipes);
      })
      .catch(setError)
      .finally(() => setLoading(false));
  }

  function getIngredients() {
    setLoading(true);

    fetch('/api/ingredients')
      .then((_) => _.json())
      .then((ingredients) => {
        setIngredients(ingredients);
      })
      .catch(setError)
      .finally(() => setLoading(false));
  }

  useEffect(() => {
    getRecipes();
    getIngredients();
  }, []);

  function refetch() {
    getRecipes();
  }

  return (
    <>
      <Navigation />
      <Container>
        {!error && loading && <Loading />}
        {error && !loading && (
          // TODO: show specific error message
          <Typography color="textPrimary" mt={3}>
            Error while loading data.
          </Typography>
        )}
        {!error && !loading && (
          <>
            <Typography color="textPrimary" variant="h2" mt={6} mb={2}>
              Recipes
              <AddRecipe ingredients={ingredients} onCompleted={refetch} />
            </Typography>
            {(!recipes || recipes?.length === 0) && (
              <Typography color="textPrimary">No recipes, yet.</Typography>
            )}
            {recipes && recipes.length > 0 && (
              <TableContainer component={Paper}>
                <Table aria-label="simple table">
                  <TableHead>
                    <TableRow>
                      <TableCell>
                        <b>Name</b>
                      </TableCell>
                      <TableCell>
                        <b>Favorite</b>
                      </TableCell>
                      <TableCell />
                    </TableRow>
                  </TableHead>
                  <TableBody>
                    {recipes
                      .sort((a, b) => {
                        if (a?.name === b?.name || !a?.name || !b?.name)
                          return 0;
                        return a.name < b.name ? -1 : 1;
                      })
                      .filter((recipe) => !!recipe)
                      .map((recipe) => (
                        <Recipe
                          key={recipe.id}
                          recipe={recipe}
                          ingredients={ingredients}
                          onCompleted={refetch}
                        />
                      ))}
                  </TableBody>
                </Table>
              </TableContainer>
            )}
          </>
        )}
      </Container>
    </>
  );
};

export default RecipesPage;
