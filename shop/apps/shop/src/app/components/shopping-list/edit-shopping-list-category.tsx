import {
  Alert,
  Box,
  Button,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  IconButton,
  Paper,
  Snackbar,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  TextField,
} from '@mui/material';
import { Edit, ShoppingCart } from 'react-feather';
import React, { useEffect, useState } from 'react';
import { HttpError } from '../error/error';
import { MenuData } from '../../pages/menus';
import fetch from 'cross-fetch';
import {
  OutputShoppingList,
  OutputShoppingListCategory,
} from '../../../schema/output-schema';
import RecipeIngredients from '../recipe/recipe-ingredients';
import { ShoppingListCategory } from './shopping-list-category';
import { IngredientData } from '../../pages/ingredients';
import { RecipeIngredient } from '../recipe-ingredients/recipe-ingredient';
import RecipeIngredientSelector from '../recipe-ingredients/recipe-ingredient-selector';
import { ShoppingListIngredient } from './shopping-list-ingredient';

export interface EditShoppingListCategoryProps {
  category: OutputShoppingListCategory;
  ingredients: IngredientData[];
  onCompleted: () => void;
}

export interface EditShoppingRequest {
  id: string;
  firstDay: string;
}

export const EditShoppingListCategory = ({
  category,
  ingredients,
  onCompleted,
}: EditShoppingListCategoryProps) => {
  const [open, setOpen] = useState(false);
  const [showConfirmation, setShowConfirmation] = useState(false);
  const [error, setError] = useState<string>('');
  const [loading, setLoading] = useState<boolean>(true);

  const handleError = (error: HttpError) =>
    error.code === 409
      ? setError('Duplicate recipe name')
      : setError(`${error.code}: ${error.message}`);

  const checkResponse = (response: Response) => {
    if (!response.ok) {
      throw new HttpError(response.status, response.statusText);
    }
  };

  const cleanUp = () => {
    setOpen(false);
  };

  const handleSave = () => {
    setOpen(false);

    //   submitApiRequest({ id, firstDay })
    //       .then((response) => checkResponse(response))
    //       .then(() => cleanUp())
    //       .then(() => onCompleted())
    //       .catch(handleError)
    //       .finally(() => setShowConfirmation(true));
  };

  return (
    <>
      <IconButton aria-label="edit" onClick={() => setOpen(true)}>
        <ShoppingCart size="18" />
      </IconButton>

      <Dialog
        open={open}
        onClose={onCompleted}
        aria-labelledby="simple-modal-title"
        aria-describedby="simple-modal-description"
        sx={{ mb: { xs: -3, md: 1 }, mt: { xs: 1 } }}
      >
        <DialogTitle
          sx={{ display: { xs: 'none', md: 'block' } }}
          id="form-dialog-title"
        >
          Edit category {category.name}
        </DialogTitle>
        <DialogContent sx={{ mb: { xs: -3, md: 1 }, mt: { xs: 0 } }}>
          <Box sx={{ mb: { xs: 0, md: 1 } }}>
            <TextField
              disabled={true}
              margin="dense"
              id="name"
              label="Name"
              type="text"
              fullWidth
              InputLabelProps={{
                shrink: true,
              }}
              value={category.name}
            />
          </Box>

          {category.ingredients && category.ingredients.length > 0 && (
            <Box ml={-3} mr={-5}>
              <TableContainer component={Paper}>
                <Table aria-label="simple table">
                  <TableHead>
                    <TableRow>
                      <TableCell>
                        <b>Name</b>
                      </TableCell>
                      <TableCell>
                        {/*<RecipeIngredientSelector value={} onChange={} options={}/>*/}
                      </TableCell>
                    </TableRow>
                  </TableHead>
                  <TableBody>
                    {category.ingredients
                      .sort((a, b) => {
                        if (a?.name === b?.name || !a?.name || !b?.name)
                          return 0;
                        return a.name < b.name ? -1 : 1;
                      })
                      .filter((ingredient) => !!ingredient)
                      .map((ingredient) => (
                        <ShoppingListIngredient
                          category={category}
                          ingredient={ingredient}
                          onCompleted={() => {}}
                        />
                      ))}
                  </TableBody>
                </Table>
              </TableContainer>
            </Box>
          )}
        </DialogContent>
        <DialogActions>
          <>
            <Button variant="contained" onClick={handleSave}>
              Close and Save
            </Button>
          </>
        </DialogActions>
      </Dialog>

      <Snackbar
        open={showConfirmation}
        autoHideDuration={4000}
        onClose={() => setShowConfirmation(false)}
      >
        <Alert severity={error ? 'error' : 'success'}>
          {error ? error : 'Done'}
        </Alert>
      </Snackbar>
    </>
  );
};
