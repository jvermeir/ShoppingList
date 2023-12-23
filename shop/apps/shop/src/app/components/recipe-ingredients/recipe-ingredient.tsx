import { IconButton, TableRow } from '@mui/material';
import { Delete } from 'react-feather';
import StyledTableCell from '../styled-components/StyledTableCell';
import React from 'react';
import { RecipeIngredientData } from '../recipe/recipe-ingredients';
import { EditRecipeIngredient } from './edit-recipe-ingredient';
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
  const handleDelete = () => {
    fetch(`/api/recipeIngredient/${recipeIngredient.id}`, {
      method: 'DELETE',
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
