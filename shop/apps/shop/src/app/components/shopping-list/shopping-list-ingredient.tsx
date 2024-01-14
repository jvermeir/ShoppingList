import { IconButton, TableRow, TextField } from '@mui/material';
import { Delete } from 'react-feather';
import StyledTableCell from '../styled-components/StyledTableCell';
import React from 'react';
import { OutputShoppingListIngredient } from '../../../schema/output-schema';
import { updateShoppingListIngredient } from 'service';

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
    fetch(`/api/shoppinglist/${shoppingListId}/ingredient/${ingredient.id}`, {
      method: 'DELETE',
    }).then((_) => onCompleted && onCompleted());
  };

  const handleUpdate = (event: React.ChangeEvent<HTMLInputElement>) => {
    const ingredientId = ingredient.id;
    const amount = Number(event.target.value);
    updateShoppingListIngredient({
      shoppingListId,
      ingredientId,
      amount,
    }).then((_) => onCompleted && onCompleted());
  };

  return (
    <TableRow key={ingredient.id} hover={true}>
      <StyledTableCell style={{ width: 1 }}>
        <IconButton
          aria-label="delete"
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
        <TextField onChange={handleUpdate} value={ingredient.amount} />
      </StyledTableCell>
      <StyledTableCell style={{ width: 100 }}>
        {ingredient.unit}
      </StyledTableCell>
    </TableRow>
  );
};
