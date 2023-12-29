import { IconButton, TableRow } from '@mui/material';
import { Delete, ShoppingCart } from 'react-feather';
import StyledTableCell from '../styled-components/StyledTableCell';
import { EditMenu } from './edit-menu';
import { RecipeData } from '../../pages/recipes';
import React from 'react';
import { MenuData } from '../../pages/menus';
import { EditShoppingList } from '../shopping-list/edit-shopping-list';

export type MenuProps = {
  menu: MenuData;
  recipes: RecipeData[];
  onCompleted: () => void;
};

export const Menu = ({ menu, recipes, onCompleted }: MenuProps) => {
  const handleDelete = () => {
    fetch(`/api/menu/${menu.id}`, {
      method: 'DELETE',
    }).then((_) => onCompleted && onCompleted());
  };

  const handleShoppingList = () => {
    // history.push('/shopping-list/' + menu.id);
  };

  return (
    <TableRow key={menu.id} hover={true}>
      <StyledTableCell>{menu.firstDay}</StyledTableCell>
      <StyledTableCell align="right">
        <EditMenu menu={menu} recipes={recipes} onCompleted={onCompleted} />
        <IconButton aria-label="delete" onClick={handleDelete}>
          <Delete size="18" />
        </IconButton>
        <IconButton
          aria-label={`shopping list for ${menu.firstDay}`}
          onClick={handleShoppingList}
        >
          <ShoppingCart size="18" />
        </IconButton>
        <EditShoppingList menu={menu} onCompleted={onCompleted} />
      </StyledTableCell>
    </TableRow>
  );
};
