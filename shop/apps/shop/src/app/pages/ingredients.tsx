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

import {Loading} from '../components/loading/loading';
import {useEffect, useState} from "react";
import {Ingredient} from "../components/ingredient/ingredient";
import {AddIngredient} from "../components/ingredient/add-ingredient";

// TODO????
import fetch from "cross-fetch";
import {CategoryData} from "./categories";

// TODO: test

export interface IngredientData {
  id: string;
  name: string;
  categoryId: string;
  categoryName: string;
}

export const IngredientsPage = () => {
  const [ingredients, setIngredients] = useState<IngredientData[]>([]);
  const [categories, setCategories] = useState<CategoryData[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<boolean>(false);

  function getIngredients() {
    setLoading(true);

    fetch('/api/ingredients-view')
      .then((_) => _.json())
      .then(ingredients => {setIngredients(ingredients)})
      .catch(setError)
      .finally(() => setLoading(false));
  }

  function getCategories() {
    setLoading(true);

    fetch('/api/categories')
      .then((_) => _.json())
      .then(categories => {setCategories(categories)})
      .catch(setError)
      .finally(() => setLoading(false));
  }

  useEffect(() => {
    getIngredients();
    getCategories();
  }, []);

  function refetch() {
    getIngredients();
  }

  return (
    <Container>
        {!error && loading && <Loading/>}
        {error && !loading && (
          <Typography color="textPrimary" mt={3}>
            Error while loading ingredients.
          </Typography>
        )}
        {!error && !loading && (
          <>
            <Typography color="textPrimary" variant="h2" mt={6} mb={2}>
              Ingredients
              <AddIngredient onCompleted={refetch} categories={categories}/>
            </Typography>
            {(!ingredients || ingredients?.length === 0) && (
              <Typography color="textPrimary">
                No ingredients, yet.
              </Typography>
            )}
            {ingredients && ingredients.length > 0 && (
              <TableContainer component={Paper}>
                <Table aria-label="simple table">
                  <TableHead>
                    <TableRow>
                      <TableCell>
                        <b>Name</b>
                      </TableCell>
                      <TableCell>
                        <b>Category</b>
                      </TableCell>
                      <TableCell/>
                    </TableRow>
                  </TableHead>
                  <TableBody>
                    {ingredients
                      .sort((a, b) => {
                        if (a?.name === b?.name || !a?.name || !b?.name) return 0;
                        return a.name < b.name ? -1 : 1;
                      })
                      .filter(ingredient => !!ingredient)
                      .map((ingredient) =>
                        <Ingredient key={ingredient.id} ingredient={ingredient} categories={categories} onCompleted={refetch}/>
                      )}
                  </TableBody>
                </Table>
              </TableContainer>
            )}
          </>
        )}
      </Container>
  );
};

// TODO: why do we need export default in this case and not for AddIngredient?
export default IngredientsPage;
