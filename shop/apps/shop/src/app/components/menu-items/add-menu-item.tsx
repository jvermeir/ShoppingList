import {
  Alert,
  Box,
  Button,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  Snackbar,
  Stack,
  TextField,
} from '@mui/material';
import React, { useEffect, useState } from 'react';
import { Plus } from 'react-feather';
import { HttpError } from '../error/error';
import { RecipeData } from '../../pages/recipes';
import RecipeSelector from './recipe-selector';
import { MenuData } from '../../pages/menus';

export interface AddMenuItemProps {
  menu: MenuData;
  recipes: RecipeData[];
  onCompleted: () => void;
}

export interface AddMenuItemRequest {
  menuId: string;
  recipeId: string;
  theDay: string;
}

export const AddMenuItem = ({
  menu,
  recipes,
  onCompleted,
}: AddMenuItemProps) => {
  const [recipeId, setRecipeId] = useState<string>('');
  const [_, setRecipeName] = useState<string>('');
  const [theDay, setTheDay] = useState<string>('');
  const [open, setOpen] = useState(false);
  const [showConfirmation, setShowConfirmation] = useState(false);
  const [error, setError] = useState<string>('');

  const submitApiRequest = (req: AddMenuItemRequest) => {
    return fetch('/api/menuItem', {
      method: 'POST',
      headers: {
        'Content-type': 'application/json; charset=UTF-8',
      },
      body: JSON.stringify(req),
    });
  };

  useEffect(() => {
    setRecipeName('');
    setTheDay('');
  }, []);

  // TODO: check error codes
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
    setRecipeName('');
    setTheDay('');
  };

  const handleSave = () => {
    setShowConfirmation(false);
    setError('');

    submitApiRequest({ menuId: menu.id, recipeId, theDay })
      .then((response) => checkResponse(response))
      .then(() => cleanUp())
      .then(() => onCompleted())
      .catch(handleError)
      .finally(() => setShowConfirmation(true));
  };

  const handleTheDay = (event: React.ChangeEvent<HTMLInputElement>) => {
    setTheDay((event.target as HTMLInputElement).value);
  };

  const handleRecipeId = (recipeId: string) => {
    setRecipeId(recipeId);
    setRecipeName(recipes.filter((recipe) => recipe.id === recipeId)[0].name);
  };

  const handleCloseAddDialog = () => {
    setOpen(false);
  };

  return (
    <>
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
          Add menu item
        </DialogTitle>
        <DialogContent sx={{ mb: { xs: -3, md: 1 }, mt: { xs: 0 } }}>
          <Box>
            <RecipeSelector
              value={recipeId}
              options={recipes}
              onChange={handleRecipeId}
            />

            <TextField
              margin="dense"
              id="theDay"
              label="Date"
              type="text"
              fullWidth
              InputLabelProps={{
                shrink: true,
              }}
              onChange={handleTheDay}
              value={theDay}
            />
          </Box>
        </DialogContent>
        {/*TODO: most changes are saved immediately. can we save them up and only save them when the user clicks "save"?*/}
        <DialogActions>
          <>
            <Button onClick={handleCloseAddDialog}>Close</Button>
            <Button variant="contained" onClick={handleSave}>
              Save
            </Button>
          </>
        </DialogActions>
      </Dialog>

      <Stack direction="row" justifyContent="end" mt={-5} mr={-4}>
        <Button
          variant="contained"
          onClick={() => setOpen(true)}
          startIcon={<Plus />}
        ></Button>
      </Stack>

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
