import {
  Alert,
  Box,
  Button,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  IconButton,
  Snackbar,
  TextField,
} from '@mui/material';
import { Edit } from 'react-feather';
import React, { useState } from 'react';
import { HttpError } from '../error/error';
import RecipeSelector from './recipe-selector';
import { RecipeData } from '../../pages/recipes';
import { MenuItemData } from '../menu/menu-items';
import { updateMenuItem } from 'service';

export interface EditMenuItemProps {
  menuItem: MenuItemData;
  recipeId: string;
  recipes: RecipeData[];
  onCompleted: () => void;
}

export const EditMenuItem = ({
  menuItem,
  recipeId,
  recipes,
  onCompleted,
}: EditMenuItemProps) => {
  const [_, setRecipeName] = useState<string>(menuItem.recipeName || '');
  const [newRecipeId, setNewRecipeId] = useState<string>(recipeId || '');
  const [theDay, setTheDay] = useState<string>(menuItem.theDay || '');
  const [open, setOpen] = useState(false);
  const [showConfirmation, setShowConfirmation] = useState(false);
  const [error, setError] = useState<string>('');

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

    updateMenuItem({
      id: menuItem.id,
      recipeId: newRecipeId,
      menuId: menuItem.menuId,
      theDay,
    })
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
    setNewRecipeId(recipeId);
    setRecipeName(recipes.filter((recipe) => recipe.id === recipeId)[0].name);
  };

  const handleCloseEditDialog = () => {
    setOpen(false);
  };

  return (
    <>
      <IconButton aria-label="edit" onClick={() => setOpen(true)}>
        <Edit size="18" />
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
          Edit recipe ingredient {theDay}
        </DialogTitle>
        <DialogContent sx={{ mb: { xs: -3, md: 1 }, mt: { xs: 0 } }}>
          <Box>
            <RecipeSelector
              value={newRecipeId}
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
            <Button onClick={handleCloseEditDialog}>Close</Button>
            <Button variant="contained" onClick={handleSave}>
              Save
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
