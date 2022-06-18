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
import {Category} from "../components/category/category";
import {AddCategory} from "../components/category/add-category";

const CategoriesPage = () => {
  const [categories, setCategories] = useState<CategoryData[]>([]);

  function getCategories() {
    console.log('getCategories');
    fetch('/api/categories')
      .then((_) => _.json())
      .then(setCategories);
  }

  useEffect(() => {
    console.log('use effect')
    getCategories();
  }, []);

  function refetch() {
    console.log('refetch');
    getCategories();
  }

  // TODO: fix this
  const error = false;
  const loading = false;
  return (
    <>
      <Container>
        {!error && loading && <Loading/>}
        {error && !loading && (
          <Typography color="textPrimary" mt={3}>
            Error while loading categories.
          </Typography>
        )}
        {!error && !loading && (
          <>
            <Typography color="textPrimary" variant="h2" mt={6} mb={2}>
              Categories
              <AddCategory onCompleted={refetch}/>
            </Typography>
            {(!categories || categories?.length === 0) && (
              <Typography color="textPrimary">
                No categories, yet.
              </Typography>
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
                      <TableCell/>
                    </TableRow>
                  </TableHead>
                  <TableBody>
                    {[...categories]
                      .sort((a, b) => {
                        if (a?.shopOrder === b?.shopOrder || !a?.shopOrder || !b?.shopOrder) return 0;
                        return a.shopOrder < b.shopOrder ? -1 : 1;
                      })
                      .map((category: CategoryData) => {
                        return category && <Category key={category?.id} category={category} onCompleted={refetch}/>
                      })}
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

export interface CategoryData {
  id: string;
  name: string;
  shopOrder: number;
}

export default CategoriesPage;
