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
  Typography,
} from '@mui/material';
import React, { useEffect, useState } from 'react';
import { Plus } from 'react-feather';
import { HttpError } from '../error/error';
import { IngredientData } from '../../pages/ingredients';
import IngredientSelector from './ingredient-selector';

export interface AddShoppingListIngredientProps {
  shoppingListId: string;
  ingredients: IngredientData[];
  onCompleted: () => void;
}

export interface AddShoppingListIngredientRequest {
  ingredientId: string;
  amount: number;
  unit: string;
}

export const AddShoppingListIngredient = ({
  shoppingListId,
  ingredients,
  onCompleted,
}: AddShoppingListIngredientProps) => {
  const [ingredientName, setIngredientName] = useState<string>('');
  const [unit, setUnit] = useState<string>('');
  const [amount, setAmount] = useState<number>(0);
  const [ingredientId, setIngredientId] = useState<string>('');
  const [open, setOpen] = useState(false);
  const [showConfirmation, setShowConfirmation] = useState(false);
  const [error, setError] = useState<string>('');

  const submitApiRequest = (req: AddShoppingListIngredientRequest) => {
    return fetch(
      `api/shoppinglist/${shoppingListId}/add/ingredient/${req.ingredientId}/amount/${req.amount}`,
      {
        method: 'PUT',
        headers: {
          'Content-type': 'application/json; charset=UTF-8',
        },
        body: JSON.stringify({}),
      }
    );
  };

  useEffect(() => {
    setIngredientName('');
    setIngredientId('');
    setUnit('');
    setAmount(0);
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
    setIngredientName('');
    setUnit('');
    setAmount(0);
  };

  const handleSave = () => {
    setShowConfirmation(false);
    setError('');

    submitApiRequest({ ingredientId, amount, unit })
      .then((response) => checkResponse(response))
      .then(() => cleanUp())
      .then(() => onCompleted())
      .catch(handleError)
      .finally(() => setShowConfirmation(true));
  };

  const handleUnit = (event: React.ChangeEvent<HTMLInputElement>) => {
    setUnit((event.target as HTMLInputElement).value);
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

  const handleCloseAddDialog = () => {
    setOpen(false);
  };

  return (
    <>
      <IconButton aria-label="edit" onClick={() => setOpen(true)}>
        <Plus size="18" />
        <Typography sx={{ ml: 1 }}>Add Ingredient</Typography>
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
          Add recipe ingredient{ingredientName}
        </DialogTitle>
        <DialogContent sx={{ mb: { xs: -3, md: 1 }, mt: { xs: 0 } }}>
          <Box>
            <IngredientSelector
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
              onChange={handleUnit}
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
            <Button onClick={handleCloseAddDialog}>Close</Button>
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
