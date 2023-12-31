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
import React, { useEffect, useState } from 'react';
import { Navigation } from '../components/navigation/navigation';
import { useParams } from 'react-router';
import fetch from 'cross-fetch';
import { OutputShoppingList } from '../../schema/output-schema';
import { ShoppingListCategory } from '../components/shopping-list/shopping-list-category';
import { IngredientData } from './ingredients';
import { CategoryData } from './categories';

export interface MenuData {
  id: string;
  firstDay: string;
}

export interface ShoppingListProps {
  firstDay: string;
}

export const ShoppingListPage = () => {
  let { firstDay } = useParams();
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<boolean>(false);
  const [shoppingList, setShoppingList] = useState<OutputShoppingList>();
  const [ingredients, setIngredients] = useState<IngredientData[]>([]);
  const [categories, setCategories] = useState<CategoryData[]>([]);

  useEffect(() => {
    getShoppinglist();
    getCategories();
    getCategories();
  }, []);

  function refetch() {
    getShoppinglist();
  }

  function getCategories() {
    setLoading(true);

    fetch('/api/categories')
      .then((_) => _.json())
      .then((categories) => {
        setCategories(categories);
      })
      .catch(setError)
      .finally(() => setLoading(false));
  }

  function getIngredients() {
    setLoading(true);

    fetch('/api/ingredientsWithDetails')
      .then((_) => _.json())
      .then((ingredients) => {
        setIngredients(ingredients);
      })
      .catch(setError)
      .finally(() => setLoading(false));
  }

  function getShoppinglist() {
    setLoading(true);

    fetch(`/api/shoppinglist/fromMenu/firstDay/${firstDay}`, {
      method: 'POST',
      headers: {
        'Content-type': 'application/json; charset=UTF-8',
      },
      body: JSON.stringify({}),
    })
      .then((_) => _.json())
      .then((shoppinglist) => {
        setShoppingList(shoppinglist);
      })
      .catch(setError)
      .finally(() => setLoading(false));
  }

  return (
    <>
      <Navigation />
      <Container>
        <>
          <Typography color="textPrimary" variant="h2" mt={6} mb={2}>
            Shopping List for {firstDay}
          </Typography>
          <TableContainer component={Paper}>
            <Table aria-label="shopping list">
              <TableHead>
                <TableRow>
                  <TableCell>
                    <b>First Day</b>
                  </TableCell>
                  <TableCell />
                </TableRow>
              </TableHead>
              <TableBody>
                {/*show message is empty*/}
                {shoppingList &&
                  shoppingList.categories
                    .sort((a, b) => {
                      if (a?.name === b?.name || !a?.name || !b?.name) return 0;
                      return a.name < b.name ? -1 : 1;
                    })
                    .filter((shoppingListCategory) => !!shoppingListCategory)
                    .map((shoppingListCategory) => (
                      <ShoppingListCategory
                        key={shoppingListCategory.id}
                        category={shoppingListCategory}
                        categories={categories}
                        ingredients={ingredients}
                        onCompleted={refetch}
                      />
                    ))}
              </TableBody>
            </Table>
          </TableContainer>
        </>
      </Container>
    </>
  );
};

export default ShoppingListPage;
