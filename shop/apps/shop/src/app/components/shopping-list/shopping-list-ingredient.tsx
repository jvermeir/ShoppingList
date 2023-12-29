import { IconButton, TableRow } from '@mui/material';
import { Delete } from 'react-feather';
import StyledTableCell from '../styled-components/StyledTableCell';
import React from 'react';
import {
  OutputShoppingListCategory,
  OutputShoppingListIngredient,
} from '../../../schema/output-schema';

type ShoppingListIngredientProps = {
  category: OutputShoppingListCategory;
  ingredient: OutputShoppingListIngredient;
  onCompleted: () => void;
};

export const ShoppingListIngredient = ({
  category,
  ingredient,
  onCompleted,
}: ShoppingListIngredientProps) => {
  // const handleDelete = () => {
  //   fetch(`/api/recipeIngredient/${recipeIngredient.id}`, {
  //     method: 'DELETE',
  //   }).then((_) => onCompleted && onCompleted());
  // };

  return (
    <TableRow key={ingredient.id} hover={true}>
      <StyledTableCell>{ingredient.name}</StyledTableCell>
      <StyledTableCell>{ingredient.amount}</StyledTableCell>
      <StyledTableCell>{ingredient.unit}</StyledTableCell>
      <StyledTableCell>
        {/*<EditRecipeIngredient*/}
        {/*  recipeIngredientData={recipeIngredient}*/}
        {/*  ingredients={ingredients}*/}
        {/*  onCompleted={onCompleted}*/}
        {/*/>*/}
        {/*<IconButton aria-label="delete" onClick={handleDelete}>*/}
        {/*  <Delete size="18" />*/}
        {/*</IconButton>*/}
      </StyledTableCell>
    </TableRow>
  );
};
