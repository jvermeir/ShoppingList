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

import { Loading } from '../loading/loading';
import React, { useEffect, useState } from 'react';

import fetch from 'cross-fetch';
import { RecipeIngredient } from '../recipe-ingredients/recipe-ingredient';
import { RecipeData } from '../../pages/recipes';
import { AddRecipeIngredient } from '../recipe-ingredients/add-recipe-ingredient';
import { IngredientData } from '../../pages/ingredients';

export interface RecipeIngredientProps {
  recipe: RecipeData;
  ingredients: IngredientData[];
  onCompleted: () => void;
}

export interface RecipeIngredientData {
  id: string;
  recipeId: string;
  ingredientId: string;
  name: string;
  amount: number;
  unit: string;
}

export const RecipeIngredientsPage = ({
  recipe,
  ingredients,
  onCompleted,
}: RecipeIngredientProps) => {
  const [recipeIngredients, setRecipeIngredients] = useState<
    RecipeIngredientData[]
  >([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<boolean>(false);

  function getRecipeIngredients(recipe: RecipeData) {
    setLoading(true);

    fetch(`/api/recipeIngredients/byRecipeId/${recipe.id}`)
      .then((_) => _.json())
      .then((recipeIngredients) => {
        setRecipeIngredients(recipeIngredients);
      })
      .catch(setError)
      .finally(() => setLoading(false));
  }

  useEffect(() => {
    getRecipeIngredients(recipe);
  }, []);

  function refetch() {
    getRecipeIngredients(recipe);
  }

  return (
    <>
      <Container>
        {!error && loading && <Loading />}
        {error && !loading && (
          <Typography color="textPrimary" mt={3}>
            Error while loading recipe ingredients.
          </Typography>
        )}
        {!error && !loading && (
          <>
            <Typography color="textPrimary" variant="h5" mt={6} mb={2}>
              Recipe ingredients
              <AddRecipeIngredient
                recipe={recipe}
                ingredients={ingredients}
                onCompleted={refetch}
              />
            </Typography>
            {(!recipeIngredients || recipeIngredients?.length === 0) && (
              <Typography color="textPrimary">
                No recipe ingredients, yet.
              </Typography>
            )}
            {recipeIngredients && recipeIngredients.length > 0 && (
              <TableContainer component={Paper}>
                <Table aria-label="simple table">
                  <TableHead>
                    <TableRow>
                      <TableCell>
                        <b>Name</b>
                      </TableCell>
                      <TableCell>
                        <b>Amount</b>
                      </TableCell>
                      <TableCell>
                        <b>Unit</b>
                      </TableCell>
                      <TableCell />
                    </TableRow>
                  </TableHead>
                  <TableBody>
                    {recipeIngredients
                      .sort((a, b) => {
                        if (a?.name === b?.name || !a?.name || !b?.name)
                          return 0;
                        return a.name < b.name ? -1 : 1;
                      })
                      .filter((recipeIngredient) => !!recipeIngredient)
                      .map((recipeIngredient) => (
                        <RecipeIngredient
                          key={recipeIngredient.id}
                          recipeIngredient={recipeIngredient}
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

export default RecipeIngredientsPage;
