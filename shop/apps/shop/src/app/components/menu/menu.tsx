import { IconButton, TableRow } from '@mui/material';
import { Delete } from 'react-feather';
import StyledTableCell from '../styled-components/StyledTableCell';
import { EditMenu } from './edit-menu';
import { RecipeData } from '../../pages/recipes';
import React from 'react';
import { MenuData } from '../../pages/menus';

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
      <StyledTableCell>
        <EditMenu menu={menu} recipes={recipes} onCompleted={onCompleted} />
        <IconButton aria-label="delete" onClick={handleDelete}>
          <Delete size="18" />
        </IconButton>
      </StyledTableCell>
    </TableRow>
  );
};
