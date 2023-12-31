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
import { ShoppingCart } from 'react-feather';
import React, { useEffect, useState } from 'react';
import { HttpError } from '../error/error';
import { MenuData } from '../../pages/menus';
import fetch from 'cross-fetch';
import { OutputShoppingList } from '../../../schema/output-schema';
import { ShoppingListCategory } from './shopping-list-category';
import { IngredientData } from '../../pages/ingredients';

export interface EditShoppingListProps {
  menu: MenuData;
  onCompleted: () => void;
}

/* TODO
  - don't query shoppinglist until required for editing
  - fix 'Each child in a list should have a unique "key" prop.' when loading shoppinglists
  - edit number/amount for ingredient
  - ? start edit on fresh page?
  - implement delete on ingredient
- save changes
 */
export interface EditShoppingRequest {
  id: string;
  firstDay: string;
}

export const EditShoppingList = ({
  menu,
  onCompleted,
}: EditShoppingListProps) => {
  const [id] = useState<string>(menu.id || '');
  const [firstDay, setFirstDay] = useState<string>(menu.firstDay || '');
  const [open, setOpen] = useState(false);
  const [showConfirmation, setShowConfirmation] = useState(false);
  const [error, setError] = useState<string>('');
  const [loading, setLoading] = useState<boolean>(true);
  const [shoppingList, setShoppingList] = useState<OutputShoppingList>();
  const [ingredients, setIngredients] = useState<IngredientData[]>([]);

  // TODO: move out of this const?
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
  useEffect(() => {
    getShoppinglist();
    console.log({ shoppingList });
  }, []);

  function getShoppinglist() {
    setLoading(true);

    fetch(`/api/shoppinglist/firstDay/${firstDay}`)
      .then((_) => _.json())
      .then((shoppinglist) => {
        console.log({ fromDb: shoppinglist });
        setShoppingList(shoppinglist);
      })
      .catch(setError)
      .finally(() => setLoading(false));

    if (!shoppingList) {
      setLoading(true);
      console.log('empty shoppinglist');
      fetch(`/api/shoppinglist/fromMenu/firstDay/${firstDay}`, {
        method: 'POST',
        headers: {
          'Content-type': 'application/json; charset=UTF-8',
        },
        body: JSON.stringify({}),
      })
        .then((_) => _.json())
        .then((shoppinglist) => {
          console.log({ newList: shoppinglist });
          setShoppingList(shoppinglist);
        })
        .catch(setError)
        .finally(() => setLoading(false));
    }
  }

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
          Edit shopping list starting {firstDay}
        </DialogTitle>
        <DialogContent sx={{ mb: { xs: -3, md: 1 }, mt: { xs: 0 } }}>
          <Box sx={{ mb: { xs: 0, md: 1 } }}>
            <TextField
              disabled={true}
              margin="dense"
              id="name"
              label="First Day"
              type="text"
              fullWidth
              InputLabelProps={{
                shrink: true,
              }}
              value={firstDay}
            />
          </Box>

          {shoppingList?.categories && shoppingList.categories.length > 0 && (
            <Box ml={-3} mr={-5}>
              <TableContainer component={Paper}>
                <Table aria-label="simple table">
                  <TableHead>
                    <TableRow>
                      <TableCell>
                        <b>Name</b>
                      </TableCell>
                      <TableCell />
                    </TableRow>
                  </TableHead>
                  <TableBody>
                    {shoppingList.categories
                      .sort((a, b) => {
                        if (a?.name === b?.name || !a?.name || !b?.name)
                          return 0;
                        return a.name < b.name ? -1 : 1;
                      })
                      .filter((category) => !!category)
                      .map((category) => (
                        <TextField value={category.name} />
                        // <ShoppingListCategory
                        //   category={category}
                        //   ingredients={ingredients}
                        //   onCompleted={() => {}}
                        // />
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
