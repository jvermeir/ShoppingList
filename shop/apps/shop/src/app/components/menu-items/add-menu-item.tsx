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
} from '@mui/material';
import { DatePicker } from '@mui/x-date-pickers/DatePicker';
import { LocalizationProvider } from '@mui/x-date-pickers/LocalizationProvider';
import { AdapterDateFns } from '@mui/x-date-pickers/AdapterDateFns';
import { nl } from 'date-fns/locale/nl';
import React, { useEffect, useState } from 'react';
import { Plus } from 'react-feather';
import { HttpError } from '../error/error';
import { RecipeData } from '../../pages/recipes';
import RecipeSelector from './recipe-selector';
import { MenuData } from '../../pages/menus';
import { postMenuItem } from 'service';

export interface AddMenuItemProps {
  menu: MenuData;
  recipes: RecipeData[];
  onCompleted: () => void;
}

export const AddMenuItem = ({
  menu,
  recipes,
  onCompleted,
}: AddMenuItemProps) => {
  const [recipeId, setRecipeId] = useState<string>('');
  const [theDay, setTheDay] = useState<Date>(new Date());
  const [open, setOpen] = useState(false);
  const [showConfirmation, setShowConfirmation] = useState(false);
  const [error, setError] = useState<string>('');

  useEffect(() => {
    setTheDay(new Date());
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
    setTheDay(new Date());
  };

  const handleSave = () => {
    setShowConfirmation(false);
    setError('');

    postMenuItem({ menuId: menu.id, recipeId, theDay: theDay.toISOString().split('T')[0] })
      .then((response) => checkResponse(response))
      .then(() => cleanUp())
      .then(() => onCompleted())
      .catch(handleError)
      .finally(() => setShowConfirmation(true));
  };

  const handleTheDay = (date: Date) => {
    setTheDay(date);
  };

  const handleRecipeId = (recipeId: string) => {
    setRecipeId(recipeId);
  };

  const handleCloseAddDialog = () => {
    setOpen(false);
  };

  return (
    <LocalizationProvider dateAdapter={AdapterDateFns} adapterLocale={nl}>
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

            <DatePicker
              label="Date"
              value={theDay}
              onChange={(value) => handleTheDay(value || new Date())}
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
    </LocalizationProvider>
  );
};
