import {
  Alert,
  Box,
  Button,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  FormControlLabel,
  Snackbar,
  TextField,
} from '@mui/material';
import React, { useEffect, useState } from 'react';
import { Plus } from 'react-feather';
import { HttpError } from '../error/error';
import Checkbox from '@mui/material/Checkbox';
import { IngredientData } from '../../pages/ingredients';

/*
add-recipe: adds name and favorite status, list of ingredients that can be edited -> recipe-ingredients.tsx
TODO: this page doesn't allow adding recipe ingredients. should it?
 */
export interface AddRecipeProps {
  ingredients: IngredientData[];
  onCompleted: () => void;
}

export interface AddRecipeRequest {
  name: string;
  favorite: boolean;
}

export const AddRecipe = ({ onCompleted, ingredients }: AddRecipeProps) => {
  const [name, setName] = useState<string>('');
  const [favorite, setFavorite] = useState<boolean>(false);
  const [open, setOpen] = useState(false);
  const [showConfirmation, setShowConfirmation] = useState(false);
  const [error, setError] = useState<string>('');

  const submitApiRequest = (req: AddRecipeRequest) => {
    return fetch('/api/recipe', {
      method: 'POST',
      headers: {
        'Content-type': 'application/json; charset=UTF-8',
      },
      body: JSON.stringify(req),
    });
  };

  const handleError = (error: HttpError) =>
    error.code === 409
      ? setError('Duplicate recipe name')
      : setError(`${error.code}: ${error.message}`);

  useEffect(() => {
    setName('');
    setFavorite(false);
  }, []);

  const checkResponse = (response: Response) => {
    if (!response.ok) {
      throw new HttpError(response.status, response.statusText);
    }
  };

  const cleanUp = () => {
    setOpen(false);
    setName('');
    setFavorite(false);
  };

  const handleSave = () => {
    setShowConfirmation(false);
    setError('');

    submitApiRequest({ name, favorite: favorite })
      .then((response) => checkResponse(response))
      .then(() => cleanUp())
      .then(() => onCompleted())
      .catch(handleError)
      .finally(() => setShowConfirmation(true));
  };

  const handleName = (event: React.ChangeEvent<HTMLInputElement>) => {
    setName((event.target as HTMLInputElement).value);
  };

  const handleFavorite = (event: React.ChangeEvent<HTMLInputElement>) => {
    setFavorite(event.target.checked);
  };

  return (
    <>
      <Button
        variant="contained"
        sx={{ float: 'right' }}
        onClick={() => setOpen(true)}
        startIcon={<Plus />}
      >
        <Box sx={{ display: { xs: 'none', sm: 'block' } }}>Add Recipe</Box>
      </Button>

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
          Add recipe {name}
        </DialogTitle>
        <DialogContent sx={{ mb: { xs: -3, md: 1 }, mt: { xs: 0 } }}>
          <Box sx={{ mb: { xs: 0, md: 1 } }}>
            <TextField
              autoFocus
              margin="dense"
              id="name"
              label="Name"
              type="text"
              fullWidth
              InputLabelProps={{
                shrink: true,
              }}
              onChange={handleName}
              value={name}
            />

            <FormControlLabel
              label="Favorite"
              control={
                <Checkbox
                  id="favorite"
                  checked={favorite}
                  onChange={handleFavorite}
                />
              }
            />
          </Box>
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
