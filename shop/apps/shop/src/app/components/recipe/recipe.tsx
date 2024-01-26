import { IconButton, TableRow } from '@mui/material';
import { Delete } from 'react-feather';
import StyledTableCell from '../styled-components/StyledTableCell';
import { EditRecipe } from './edit-recipe';
import { RecipeData } from '../../pages/recipes';
import Checkbox from '@mui/material/Checkbox';
import React from 'react';
import { IngredientData } from 'service';

// TODO: add amounts for ingredients per id

export type RecipeProps = {
  recipe: RecipeData;
  ingredients: IngredientData[];
  onCompleted: () => void;
};

export const Recipe = ({ recipe, ingredients, onCompleted }: RecipeProps) => {
  const handleDelete = () => {
    fetch(`/api/recipe/${recipe.id}`, {
      method: 'DELETE',
    }).then((_) => onCompleted && onCompleted());
  };

  const updateRecipe = (event: React.ChangeEvent<HTMLInputElement>) => {
    fetch(`/api/recipe`, {
      method: 'PUT',
      headers: {
        'Content-type': 'application/json; charset=UTF-8',
      },
      body: JSON.stringify({
        id: recipe.id,
        name: recipe.name,
        favorite: (event.target as HTMLInputElement).checked,
      }),
    }).then((_) => onCompleted && onCompleted());
  };

  return (
    <TableRow key={recipe.id} hover={true}>
      <StyledTableCell>{recipe.name}</StyledTableCell>
      <StyledTableCell>
        <Checkbox checked={recipe.favorite} onChange={updateRecipe} />
      </StyledTableCell>
      <StyledTableCell>
        <EditRecipe
          recipe={recipe}
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
