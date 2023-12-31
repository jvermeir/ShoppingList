import { Button, IconButton, TableRow } from '@mui/material';
import { Delete, ShoppingCart } from 'react-feather';
import StyledTableCell from '../styled-components/StyledTableCell';
import { EditMenu } from './edit-menu';
import { RecipeData } from '../../pages/recipes';
import React from 'react';
import { MenuData } from '../../pages/menus';
import { EditShoppingList } from '../shopping-list/edit-shopping-list';
import { Navigate } from 'react-router';
import { Link } from 'react-router-dom';
import ShoppingList from '../../pages/shopping-list';

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
    const target = `/shopping-list/${menu.id}`;
    console.log({ target });
    return <Navigate to={target} />;
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
        {/*<IconButton*/}
        {/*  aria-label={`shopping list for ${menu.firstDay}`}*/}
        {/*  // component={ShoppingList}*/}
        {/*  href={`/shoppingList?firstDay=${menu.firstDay}`}*/}
        {/*  // to="/shopping-list"*/}
        {/*>*/}
        {/*  <ShoppingCart size="18" />*/}
        {/*</IconButton>*/}
        {/*<EditShoppingList menu={menu} onCompleted={onCompleted} />*/}
      </StyledTableCell>
    </TableRow>
  );
};
