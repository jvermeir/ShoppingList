import { IconButton, TableRow } from '@mui/material';
import { Delete } from 'react-feather';
import StyledTableCell from '../styled-components/StyledTableCell';
import React, { useState } from 'react';
import { RecipeIngredientData } from '../recipe/recipe-ingredients';
import { EditRecipeIngredient } from './edit-recipe-ingredient';
import { AddRecipeIngredient } from './add-recipe-ingredient';
import { IngredientData } from '../../pages/ingredients';
import fetch from 'cross-fetch';

type RecipeProps = {
  recipeIngredient: RecipeIngredientData;
  ingredients: IngredientData[];
  onCompleted: () => void;
};

export const RecipeIngredient = ({
  recipeIngredient,
  ingredients,
  onCompleted,
}: RecipeProps) => {
  const [error, setError] = useState<boolean>(false);

  const handleDelete = () => {
    fetch(`/api/recipeIngredient/${recipeIngredient.id}`, {
      method: 'DELETE',
    }).then((_) => onCompleted && onCompleted());
  };

  // TODO: update recipeIngredient ?
  const updateRecipe = (event: React.ChangeEvent<HTMLInputElement>) => {
    fetch(`/api/recipe`, {
      method: 'PUT',
      headers: {
        'Content-type': 'application/json; charset=UTF-8',
      },
      body: JSON.stringify({
        id: recipeIngredient.id,
        name: recipeIngredient.name,
        favorite: (event.target as HTMLInputElement).checked,
      }),
    }).then((_) => onCompleted && onCompleted());
  };

  return (
    <TableRow key={recipeIngredient.id} hover={true}>
      <StyledTableCell>{recipeIngredient.name}</StyledTableCell>
      <StyledTableCell>{recipeIngredient.amount}</StyledTableCell>
      <StyledTableCell>{recipeIngredient.unit}</StyledTableCell>
      <StyledTableCell>
        <EditRecipeIngredient
          recipeIngredientData={recipeIngredient}
          ingredients={ingredients}
          onCompleted={onCompleted}
        />
        <IconButton aria-label="delete" onClick={handleDelete}>
          <Delete size="18" />
        </IconButton>
      </StyledTableCell>
    </TableRow>
  );
};
