import { IconButton, TableRow } from '@mui/material';
import { Delete } from 'react-feather';
import StyledTableCell from '../styled-components/StyledTableCell';
import React from 'react';
import fetch from 'cross-fetch';
import { OutputShoppingListCategory } from '../../../schema/output-schema';
import { EditShoppingListCategory } from './edit-shopping-list-category';

type ShoppingListCategoryProps = {
  shoppingListId: string;
  category: OutputShoppingListCategory;
  onCompleted: () => void;
};

export const ShoppingListCategory = ({
  shoppingListId,
  category,
  onCompleted,
}: ShoppingListCategoryProps) => {
  const handleDelete = () => {
    fetch(`/api/shoppinglist/${shoppingListId}/category/${category.id}`, {
      method: 'DELETE',
    }).then((_) => onCompleted && onCompleted());
  };

  return (
    <TableRow key={category.id} hover={true}>
      <StyledTableCell style={{ width: 10 }}>
        <IconButton aria-label="delete" onClick={handleDelete}>
          <Delete size="18" />
        </IconButton>
        {category.name}
      </StyledTableCell>
      <StyledTableCell style={{ width: 100 }}>
        <EditShoppingListCategory
          shoppingListId={shoppingListId}
          category={category}
          onCompleted={onCompleted}
        />
      </StyledTableCell>
    </TableRow>
  );
};
