import {
  Container,
  Paper,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Typography,
} from '@mui/material';

import { Loading } from '../loading/loading';
import React, { useEffect, useState } from 'react';

import fetch from 'cross-fetch';
import { RecipeData } from '../../pages/recipes';
import Box from '@mui/material/Box';
import { MenuData } from '../../pages/menus';
import { MenuItem } from '../menu-items/menu-item';
import { AddMenuItem } from '../menu-items/add-menu-item';
import { OutputMenuItem } from '../../../data/OutputSchema';

export interface MenuItemProps {
  menu: MenuData;
  recipes: RecipeData[];
  onCompleted: () => void;
}

export interface MenuItemData {
  id: string;
  menuId: string;
  recipeId: string;
  recipeName: string;
  theDay: string;
}

export const MenuItems = ({ menu, recipes }: MenuItemProps) => {
  const [menuItems, setMenuItems] = useState<MenuItemData[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<boolean>(false);

  function getMenuItems(menu: MenuData) {
    setLoading(true);

    fetch(`/api/menu/details/firstDay/${menu.firstDay}`)
      .then((_) => _.json())
      .then((menuWithDetails) => {
        const mis = menuWithDetails.menuItems.map(
          (menuItem: OutputMenuItem) => {
            return {
              id: menuItem.id,
              menuId: menuWithDetails.id,
              recipeId: menuItem.recipe.id,
              recipeName: menuItem.recipe.name,
              theDay: menuItem.theDay,
            };
          }
        );
        setMenuItems(mis);
      })
      .catch(setError)
      .finally(() => setLoading(false));
  }

  useEffect(() => {
    getMenuItems(menu);
  }, []);

  function refetch() {
    getMenuItems(menu);
  }

  return (
    <>
      <Container>
        {!error && loading && <Loading />}
        {error && !loading && (
          <Typography color="textPrimary" mt={3}>
            Error while loading recipe ingredients.
          </Typography>
        )}
        {!error && !loading && (
          <>
            <Typography color="textPrimary" variant="h5" mt={1} mb={2} ml={-3}>
              Menu items
              <AddMenuItem
                menu={menu}
                recipes={recipes}
                onCompleted={refetch}
              />
            </Typography>
            {(!menuItems || menuItems?.length === 0) && (
              <Typography color="textPrimary">No menu items, yet.</Typography>
            )}
            {menuItems && menuItems.length > 0 && (
              <Box ml={-3} mr={-5}>
                <TableContainer component={Paper}>
                  <Table aria-label="simple table">
                    <TableHead>
                      <TableRow>
                        <TableCell>
                          <b>Date</b>
                        </TableCell>
                        <TableCell>
                          <b>Recipe</b>
                        </TableCell>
                      </TableRow>
                    </TableHead>
                    <TableBody>
                      {menuItems
                        .sort((a, b) => {
                          if (
                            a?.theDay === b?.theDay ||
                            !a?.theDay ||
                            !b?.theDay
                          )
                            return 0;
                          return a.theDay < b.theDay ? -1 : 1;
                        })
                        .filter((menuItem) => !!menuItem)
                        .map((menuItem) => (
                          <>
                            <MenuItem
                              key={menuItem.id}
                              menuItem={menuItem}
                              recipes={recipes}
                              onCompleted={refetch}
                            />
                          </>
                        ))}
                    </TableBody>
                  </Table>
                </TableContainer>
              </Box>
            )}
          </>
        )}
      </Container>
    </>
  );
};
