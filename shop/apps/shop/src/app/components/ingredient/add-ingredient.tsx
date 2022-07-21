import {
  Alert,
  Box,
  Button,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  Snackbar,
  TextField
} from "@mui/material";
import React, {useState} from "react";
import {Plus} from "react-feather";
import {HttpError} from "../error/error";
import CategorySelector from "../category/category-selector";
import {CategoryData} from "../../pages/categories";

export interface AddIngredientProps {
  categories: CategoryData[];
  onCompleted: () => void;
}

export interface AddIngredientRequest {
  name: string;
  categoryId: string;
}

// TODO: why does this form show previous values? unless set to empty/0 explicitly

export const AddIngredient = ({onCompleted, categories}: AddIngredientProps) => {
  const [name, setName] = useState<string>('')
  const [categoryId, setCategoryId] = useState<string>('')
  const [categoryName, setCategoryName] = useState<string>('')
  const [open, setOpen] = useState(false);
  const [showConfirmation, setShowConfirmation] = useState(false);
  const [error, setError] = useState<string>('');

  const submitApiRequest = (req: AddIngredientRequest) => {
    return fetch('/api/ingredient', {
      method: 'POST',
      headers: {
        "Content-type": "application/json; charset=UTF-8"
      },
      body: JSON.stringify(req),
    });
  };

  const handleError = (error: HttpError) =>
    error.code === 409 ? setError('Duplicate ingredient name') : setError(`${error.code}: ${error.message}`);

  const checkResponse = (response: Response) => {
    if (!response.ok) {
      throw new HttpError(response.status, response.statusText);
    }
  }

  const cleanUp = () => {
    setOpen(false);
    setName('');
    setCategoryId('');
  }

  const handleSave = () => {
    setShowConfirmation(false);
    setError('');

    submitApiRequest({name, categoryId})
      .then((response) => checkResponse(response))
      .then(() => cleanUp())
      .then(() => onCompleted())
      .catch(handleError)
      .finally(() => setShowConfirmation(true));
  }

  const handleName = (event: React.ChangeEvent<HTMLInputElement>) => {
    setName((event.target as HTMLInputElement).value);
  };

  const handleCategoryId = (categoryId: string) => {
    setCategoryId(categoryId);
    setCategoryName(categories.filter((category) => category.id === categoryId)[0].id)
  };

  const handleCloseAddDialog = () => {
    setOpen(false);
  };

  return (
    <>
      <Button variant="contained" sx={{float: 'right'}} onClick={() => setOpen(true)} startIcon={<Plus/>}>
        <Box sx={{display: {xs: 'none', sm: 'block'}}}>
          Add Ingredient
        </Box>
      </Button>

      <Dialog
        open={open}
        onClose={onCompleted}
        aria-labelledby="simple-modal-title"
        aria-describedby="simple-modal-description"
        sx={{mb: {xs: -3, md: 1}, mt: {xs: 1}}}
      >
        <DialogTitle sx={{display: {xs: 'none', md: 'block'}}} id="form-dialog-title">Add Ingredient</DialogTitle>
        <DialogContent sx={{mb: {xs: -3, md: 1}, mt: {xs: 0}}}>
          <Box sx={{mb: {xs: 0, md: 1}}}>
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
              <CategorySelector value={categoryName} options={categories} onChange={handleCategoryId}/>
            </Box>

          </Box>
        </DialogContent>
        <DialogActions>
          <>
            <Button onClick={handleCloseAddDialog}>Close</Button>
            <Button variant="contained" onClick={handleSave}>Save</Button>
          </>
        </DialogActions>
      </Dialog>

      <Snackbar
        open={showConfirmation}
        autoHideDuration={4000}
        onClose={() => setShowConfirmation(false)}
      >
        <Alert severity={error ? 'error' : 'success'}>{error ? error : 'Done'}</Alert>
      </Snackbar>
    </>
  );
}
