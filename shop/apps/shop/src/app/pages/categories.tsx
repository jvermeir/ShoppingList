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
import { useEffect, useState } from 'react';
import { Category } from '../components/category/category';
import { AddCategory } from '../components/category/add-category';
import { Navigation } from '../components/navigation/navigation';
import { CategoryData, getCategories } from 'service';

export const CategoriesPage = () => {
  const [categories, setCategories] = useState<CategoryData[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<boolean>(false);

  useEffect(() => {
    getCategories(setCategories, setLoading, setError);
  }, []);

  function refetch() {
    getCategories(setCategories, setLoading, setError);
  }

  return (
    <>
      <Navigation />
      <Container>
        {!error && loading && <Loading />}
        {error && !loading && (
          <Typography color="textPrimary" mt={3}>
            Error while loading categories.
          </Typography>
        )}
        {!error && !loading && (
          <>
            <Typography color="textPrimary" variant="h2" mt={6} mb={2}>
              Categories
              <AddCategory onCompleted={refetch} />
            </Typography>
            {(!categories || categories?.length === 0) && (
              <Typography color="textPrimary">No categories, yet.</Typography>
            )}
            {categories && categories.length > 0 && (
              <TableContainer component={Paper}>
                <Table aria-label="simple table">
                  <TableHead>
                    <TableRow>
                      <TableCell>
                        <b>Name</b>
                      </TableCell>
                      <TableCell>
                        <b>Shop order</b>
                      </TableCell>
                      <TableCell />
                    </TableRow>
                  </TableHead>
                  <TableBody>
                    {categories
                      .sort((a, b) => {
                        if (
                          a?.shopOrder === b?.shopOrder ||
                          !a?.shopOrder ||
                          !b?.shopOrder
                        )
                          return 0;
                        return a.shopOrder < b.shopOrder ? -1 : 1;
                      })
                      .filter((category) => !!category)
                      .map((category) => (
                        <Category
                          key={category.id}
                          category={category}
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

// TODO: why do we need export default in this case and not for AddCategory?
export default CategoriesPage;
