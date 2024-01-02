import { IconButton, TableRow } from '@mui/material';
import { Delete, ShoppingCart } from 'react-feather';
import StyledTableCell from '../styled-components/StyledTableCell';
import { EditMenu } from './edit-menu';
import { RecipeData } from '../../pages/recipes';
import React from 'react';
import { MenuData } from '../../pages/menus';
import { Link } from 'react-router-dom';

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
          component={Link}
          to={`/shopping-list/${menu.firstDay}`}
          color="primary"
        >
          <ShoppingCart size="18" />
        </IconButton>
      </StyledTableCell>
    </TableRow>
  );
};
