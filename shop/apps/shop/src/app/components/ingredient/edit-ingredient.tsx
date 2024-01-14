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
import CategorySelector from '../category/category-selector';
import React, { useState } from 'react';
import { HttpError } from '../error/error';
import { CategoryData, IngredientData, updateIngredient } from 'service';

export interface EditIngredientProps {
  ingredient: IngredientData;
  categories: CategoryData[];
  onCompleted: () => void;
}

export const EditIngredient = ({
  ingredient,
  categories,
  onCompleted,
}: EditIngredientProps) => {
  const [id] = useState<string>(ingredient.id || '');
  const [name, setName] = useState<string>(ingredient.name || '');
  const [categoryId, setCategoryId] = useState<string>(
    ingredient.categoryId || ''
  );
  const [unit, setUnit] = useState<string>(ingredient.unit || '');
  const [categoryName, setCategoryName] = useState<string>(
    ingredient.categoryId || ''
  );
  const [open, setOpen] = useState(false);
  const [showConfirmation, setShowConfirmation] = useState(false);
  const [error, setError] = useState<string>('');

  const handleError = (error: HttpError) =>
    error.code === 409
      ? setError('Duplicate ingredient name')
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

    updateIngredient({ id, name, categoryId: categoryId, unit: unit })
      .then((response) => checkResponse(response))
      .then(() => cleanUp())
      .then(() => onCompleted())
      .catch(handleError)
      .finally(() => setShowConfirmation(true));
  };

  const handleName = (event: React.ChangeEvent<HTMLInputElement>) => {
    setName((event.target as HTMLInputElement).value);
  };

  const handleUnit = (event: React.ChangeEvent<HTMLInputElement>) => {
    setUnit((event.target as HTMLInputElement).value);
  };

  const handleCategoryId = (categoryId: string) => {
    setCategoryId(categoryId);
    setCategoryName(
      categories.filter(
        (category: CategoryData) => category.id === categoryId
      )[0].id
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
          Edit ingredient '{name}'
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

            <Box>
              <CategorySelector
                value={categoryName}
                options={categories}
                onChange={handleCategoryId}
              />

              <TextField
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
            </Box>
          </Box>
        </DialogContent>
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
