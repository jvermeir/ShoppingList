import { IconButton, TableRow } from '@mui/material';
import { Delete } from 'react-feather';
import StyledTableCell from '../styled-components/StyledTableCell';
import React from 'react';
import fetch from 'cross-fetch';
import { IngredientData } from '../../pages/ingredients';
import { CategoryData } from '../../pages/categories';
import {
  OutputShoppingListCategory,
  OutputShoppingListIngredient,
} from '../../../schema/output-schema';
import { EditShoppingListCategory } from './edit-shopping-list-category';

type ShoppingListCategoryProps = {
  category: OutputShoppingListCategory;
  categories: CategoryData[];
  ingredients: IngredientData[];
  onCompleted: () => void;
};

export const ShoppingListCategory = ({
  category,
  categories,
  ingredients,
  onCompleted,
}: ShoppingListCategoryProps) => {
  // const handleDelete = () => {
  //   fetch(`/api/menuItem/${menuItem.id}`, {
  //     method: 'DELETE',
  //   }).then((_) => onCompleted && onCompleted());
  // };

  return (
    <TableRow key={category.id} hover={true}>
      <StyledTableCell>{category.name}</StyledTableCell>
      <StyledTableCell>
        <EditShoppingListCategory
          category={category}
          ingredients={ingredients}
          onCompleted={onCompleted}
        />
        {/*<IconButton aria-label="delete" onClick={handleDelete}>*/}
        {/*  <Delete size="18" />*/}
        {/*</IconButton>*/}
      </StyledTableCell>
    </TableRow>
  );
};
