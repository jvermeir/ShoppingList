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
import { DatePicker } from '@mui/x-date-pickers/DatePicker';
import { AdapterDateFns } from '@mui/x-date-pickers/AdapterDateFnsV3';
import { nl } from 'date-fns/locale/nl';
import { LocalizationProvider } from '@mui/x-date-pickers/LocalizationProvider';

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
  const [theDay, setTheDay] = useState<Date>(
    new Date(menuItem.theDay + 'T00:00:00') || new Date()
  );
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
    console.log(`handleSave: ${theDay.toISOString().split('T')[0]}`);
    updateMenuItem({
      id: menuItem.id,
      recipeId: newRecipeId,
      menuId: menuItem.menuId,
      theDay: theDay.toISOString().split('T')[0],
    })
      .then((response) => checkResponse(response))
      .then(() => cleanUp())
      .then(() => onCompleted())
      .catch(handleError)
      .finally(() => setShowConfirmation(true));
  };

  const handleTheDay = (date: Date) => {
    console.log(`date: ${date}`);
    setTheDay(date);
  };

  const handleRecipeId = (recipeId: string) => {
    setNewRecipeId(recipeId);
    setRecipeName(recipes.filter((recipe) => recipe.id === recipeId)[0].name);
  };

  const handleCloseEditDialog = () => {
    setOpen(false);
  };

  return (
    <LocalizationProvider dateAdapter={AdapterDateFns} adapterLocale={nl}>
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
          Edit recipe ingredient {theDay.toISOString().split('T')[0]}
        </DialogTitle>
        <DialogContent sx={{ mb: { xs: -3, md: 1 }, mt: { xs: 0 } }}>
          <Box>
            <RecipeSelector
              value={newRecipeId}
              options={recipes}
              onChange={handleRecipeId}
            />

            <DatePicker
              label="Controlled picker"
              value={theDay}
              onChange={(value) => handleTheDay(value || new Date())}
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
    </LocalizationProvider>
  );
};
