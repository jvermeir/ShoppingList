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

import { Loading } from '../components/loading/loading';
import React, { useEffect, useState } from 'react';

import fetch from 'cross-fetch';
import { Navigation } from '../components/navigation/navigation';
import { RecipeData } from './recipes';
import { AddMenu } from '../components/menu/add-menu';
import { Menu } from '../components/menu/menu';

export interface MenuData {
  id: string;
  firstDay: string;
}

export const MenusPage = () => {
  const [menus, setMenus] = useState<MenuData[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<boolean>(false);
  const [recipes, setRecipes] = useState<RecipeData[]>([]);

  useEffect(() => {
    getMenus();
    getRecipes();
  }, []);

  function refetch() {
    getMenus();
  }

  function getRecipes() {
    setLoading(true);

    fetch('/api/recipes')
      .then((_) => _.json())
      .then((recipes) => {
        setRecipes(recipes);
      })
      .catch(setError)
      .finally(() => setLoading(false));
  }

  function getMenus() {
    setLoading(true);

    fetch('/api/menus')
      .then((_) => _.json())
      .then((menus) => {
        setMenus(menus);
      })
      .catch(setError)
      .finally(() => setLoading(false));
  }

  return (
    <>
      <Navigation />
      <Container>
        {!error && loading && <Loading />}
        {error && !loading && (
          // TODO: show specific error message
          <Typography color="textPrimary" mt={3}>
            Error while loading data.
          </Typography>
        )}
        {!error && !loading && (
          <>
            <Typography color="textPrimary" variant="h2" mt={6} mb={2}>
              Menus
              <AddMenu recipes={recipes} onCompleted={refetch} />
            </Typography>
            {(!menus || menus?.length === 0) && (
              <Typography color="textPrimary">No menus, yet.</Typography>
            )}
            {menus && menus.length > 0 && (
              <TableContainer component={Paper}>
                <Table aria-label="simple table">
                  <TableHead>
                    <TableRow>
                      <TableCell>
                        <b>First Day</b>
                      </TableCell>
                      <TableCell />
                    </TableRow>
                  </TableHead>
                  <TableBody>
                    {menus
                      .sort((a, b) => {
                        if (
                          a?.firstDay === b?.firstDay ||
                          !a?.firstDay ||
                          !b?.firstDay
                        )
                          return 0;
                        return a.firstDay < b.firstDay ? -1 : 1;
                      })
                      .filter((menu) => !!menu)
                      .map((menu) => (
                        <Menu
                          key={menu.id}
                          menu={menu}
                          recipes={recipes}
                          onCompleted={refetch}
                        />
                      ))}
                  </TableBody>
                </Table>
              </TableContainer>
            )}
          </>
        )}
      </Container>
    </>
  );
};

export default MenusPage;
