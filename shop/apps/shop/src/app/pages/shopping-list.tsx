import { Container, Typography } from '@mui/material';

import { Loading } from '../components/loading/loading';
import React, { useEffect, useState } from 'react';
import { Navigation } from '../components/navigation/navigation';
import { RecipeData } from './recipes';
import { useParams } from 'react-router';

export interface MenuData {
  id: string;
  firstDay: string;
}

export interface ShoppingListProps {
  id: string;
}

export const ShoppingListPage = () => {
  let { id } = useParams();
  const [menus, setMenus] = useState<MenuData[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<boolean>(false);
  const [recipes, setRecipes] = useState<RecipeData[]>([]);

  useEffect(() => {}, []);

  function refetch() {}

  return (
    <>
      <Navigation />
      <Container>
        <>
          <Typography color="textPrimary" variant="h2" mt={6} mb={2}>
            Shopping List for {id}
          </Typography>
        </>
      </Container>
    </>
  );
};

export default ShoppingListPage;
