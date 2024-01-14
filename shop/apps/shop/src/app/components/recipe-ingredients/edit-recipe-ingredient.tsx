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
import { RecipeIngredientData } from '../recipe/recipe-ingredients';
import RecipeIngredientSelector from './recipe-ingredient-selector';
import { IngredientData, updateRecipeIngredient } from 'service';

export interface EditRecipeIngredientProps {
  recipeIngredientData: RecipeIngredientData;
  ingredients: IngredientData[];
  onCompleted: () => void;
}

export const EditRecipeIngredient = ({
  recipeIngredientData,
  ingredients,
  onCompleted,
}: EditRecipeIngredientProps) => {
  const [id] = useState<string>(recipeIngredientData.id || '');
  const [recipeId] = useState<string>(recipeIngredientData.recipeId || '');
  const [name] = useState<string>(recipeIngredientData.name || '');
  const [_, setIngredientName] = useState<string>(
    recipeIngredientData.name || ''
  );
  const [ingredientId, setIngredientId] = useState<string>(
    recipeIngredientData.ingredientId || ''
  );
  const [amount, setAmount] = useState<number>(
    recipeIngredientData.amount || 0
  );
  const [unit, setUnit] = useState<string>(recipeIngredientData.unit || '');
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

    updateRecipeIngredient({
      id,
      recipeId,
      ingredientId,
      unit,
      amount,
    })
      .then((response) => checkResponse(response))
      .then(() => cleanUp())
      .then(() => onCompleted())
      .catch(handleError)
      .finally(() => setShowConfirmation(true));
  };

  const handleAmount = (event: React.ChangeEvent<HTMLInputElement>) => {
    // TODO: is there a better way to get a number from the event?
    setAmount((event.target as HTMLInputElement).value as unknown as number);
  };

  const handleIngredientId = (ingredientId: string) => {
    setIngredientId(ingredientId);
    setIngredientName(
      ingredients.filter((ingredient) => ingredient.id === ingredientId)[0].name
    );
    setUnit(
      ingredients.filter((ingredient) => ingredient.id === ingredientId)[0].unit
    );
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
          Edit recipe ingredient {name}
        </DialogTitle>
        <DialogContent sx={{ mb: { xs: -3, md: 1 }, mt: { xs: 0 } }}>
          <Box>
            <RecipeIngredientSelector
              value={ingredientId}
              options={ingredients}
              onChange={handleIngredientId}
            />

            <TextField
              disabled={true}
              margin="dense"
              id="unit"
              label="Unit"
              type="text"
              fullWidth
              InputLabelProps={{
                shrink: true,
              }}
              onChange={() => {}}
              value={unit}
            />

            <TextField
              margin="dense"
              id="amount"
              label="Amount"
              type="number"
              fullWidth
              InputLabelProps={{
                shrink: true,
              }}
              onChange={handleAmount}
              value={amount}
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
