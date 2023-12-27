import { IconButton, TableRow } from '@mui/material';
import { Delete } from 'react-feather';
import StyledTableCell from '../styled-components/StyledTableCell';
import React from 'react';
import fetch from 'cross-fetch';
import { RecipeData } from '../../pages/recipes';
import { EditMenuItem } from './edit-menu-item';
import { MenuItemData } from '../menu/menu-items';

type MenuItemProps = {
  menuItem: MenuItemData;
  recipes: RecipeData[];
  onCompleted: () => void;
};

export const MenuItem = ({ menuItem, recipes, onCompleted }: MenuItemProps) => {
  const handleDelete = () => {
    fetch(`/api/menuItem/${menuItem.id}`, {
      method: 'DELETE',
    }).then((_) => onCompleted && onCompleted());
  };

  return (
    <TableRow key={menuItem.id} hover={true}>
      <StyledTableCell>{menuItem.theDay}</StyledTableCell>
      <StyledTableCell>{menuItem.recipeName}</StyledTableCell>
      <StyledTableCell>
        <EditMenuItem
          menuItem={menuItem}
          recipeId={menuItem.recipeId}
          recipes={recipes}
          onCompleted={onCompleted}
        />
        <IconButton aria-label="delete" onClick={handleDelete}>
          <Delete size="18" />
        </IconButton>
      </StyledTableCell>
    </TableRow>
  );
};
