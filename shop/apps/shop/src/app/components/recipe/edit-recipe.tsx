import {
  Alert,
  Box,
  Button,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  FormControlLabel,
  IconButton,
  Snackbar,
  TextField,
} from '@mui/material';
import { Edit } from 'react-feather';
import React, { useState } from 'react';
import { HttpError } from '../error/error';
import { RecipeData } from '../../pages/recipes';
import Checkbox from '@mui/material/Checkbox';
import RecipeIngredients from './recipe-ingredients';
import { IngredientData, updateRecipe } from 'service';

export interface EditRecipeProps {
  recipe: RecipeData;
  ingredients: IngredientData[];
  onCompleted: () => void;
}

export const EditRecipe = ({
  recipe,
  ingredients,
  onCompleted,
}: EditRecipeProps) => {
  const [id] = useState<string>(recipe.id || '');
  const [name, setName] = useState<string>(recipe.name || '');
  const [favorite, setFavorite] = useState<boolean>(recipe.favorite || false);
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

    updateRecipe({ id, name, favorite })
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
          Edit recipe {name}
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
            <RecipeIngredients
              recipe={recipe}
              ingredients={ingredients}
              onCompleted={() => {}}
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
