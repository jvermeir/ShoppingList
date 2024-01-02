import {
  Container,
  IconButton,
  Paper,
  Table,
  TableBody,
  TableContainer,
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
import { Loading } from '../components/loading/loading';
import { RefreshCcw } from 'react-feather';
import { AddShoppingListIngredient } from '../components/shopping-list/add-shopping-list-ingredient';

export const ShoppingListPage = () => {
  let { firstDay } = useParams();
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<boolean>(false);
  const [shoppingList, setShoppingList] = useState<OutputShoppingList>();
  const [ingredients, setIngredients] = useState<IngredientData[]>([]);
  const [_, setCategories] = useState<CategoryData[]>([]);

  useEffect(() => {
    getShoppinglist();
    getCategories();
    getIngredients();
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

  function reloadFromMenu() {
    setLoading(true);

    fetch(`/api/shoppinglist/reloadFromMenu/firstDay/${firstDay}`, {
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
              Shopping List for {firstDay}
            </Typography>
            <AddShoppingListIngredient
              shoppingListId={shoppingList?.id!!}
              ingredients={ingredients}
              onCompleted={refetch}
            />
            <IconButton
              sx={{ ml: 2 }}
              aria-label="edit"
              onClick={() => {
                reloadFromMenu();
              }}
            >
              <RefreshCcw size="18" />
              <Typography sx={{ ml: 1 }}>Reload from menu</Typography>
            </IconButton>

            {!shoppingList ||
              (shoppingList.categories.length == 0 && (
                <Typography mt={2} variant="h4">
                  Add ingredients or use refresh
                </Typography>
              ))}
            <TableContainer component={Paper}>
              <Table aria-label="shopping list">
                <TableBody>
                  {shoppingList &&
                    shoppingList.categories
                      .sort((a, b) => {
                        if (a?.name === b?.name || !a?.name || !b?.name)
                          return 0;
                        return a.name < b.name ? -1 : 1;
                      })
                      .filter((shoppingListCategory) => !!shoppingListCategory)
                      .map((shoppingListCategory) => (
                        <ShoppingListCategory
                          key={shoppingListCategory.id}
                          shoppingListId={shoppingList.id}
                          category={shoppingListCategory}
                          onCompleted={refetch}
                        />
                      ))}
                </TableBody>
              </Table>
            </TableContainer>
          </>
        )}
      </Container>
    </>
  );
};

export default ShoppingListPage;
