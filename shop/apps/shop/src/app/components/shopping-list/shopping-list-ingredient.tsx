import { IconButton, TableRow } from '@mui/material';
import { Delete } from 'react-feather';
import StyledTableCell from '../styled-components/StyledTableCell';
import React from 'react';
import { OutputShoppingListIngredient } from '../../../schema/output-schema';

type ShoppingListIngredientProps = {
  shoppingListId: string;
  ingredient: OutputShoppingListIngredient;
  onCompleted: () => void;
};

export const ShoppingListIngredient = ({
  shoppingListId,
  ingredient,
  onCompleted,
}: ShoppingListIngredientProps) => {
  const handleDelete = () => {
    fetch(`/api/shoppingList/${shoppingListId}/ingredient/${ingredient.id}`, {
      method: 'DELETE',
    }).then((_) => onCompleted && onCompleted());
  };

  return (
    <TableRow key={ingredient.id} hover={true}>
      <StyledTableCell style={{ width: 10 }}>
        <IconButton
          aria-label="edit"
          onClick={() => {
            handleDelete();
          }}
        >
          <Delete size="18" />
        </IconButton>
      </StyledTableCell>
      <StyledTableCell style={{ width: 100 }}>
        {ingredient.name}
      </StyledTableCell>
      <StyledTableCell style={{ width: 100 }}>
        {ingredient.amount}
      </StyledTableCell>
      <StyledTableCell style={{ width: 100 }}>
        {ingredient.unit}
      </StyledTableCell>
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
