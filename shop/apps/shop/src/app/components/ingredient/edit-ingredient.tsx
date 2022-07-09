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
  TextField
} from "@mui/material";
import {Edit} from "react-feather";
import {IngredientData} from "../../pages/ingredients";
import React, {useState} from "react";
import {HttpError} from "../error/error";

export interface EditIngredientProps {
  ingredient: IngredientData,
  onCompleted: () => void
}

export interface EditIngredientRequest {
  id: string;
  name: string;
  categoryId: string;
}

export const EditIngredient = ({ingredient, onCompleted}: EditIngredientProps) => {
  const [id, setId] = useState<string>(ingredient.id || '')
  const [name, setName] = useState<string>(ingredient.name || '')
  const [categoryId, setCategoryId] = useState<string>(ingredient.categoryId || '')
  const [open, setOpen] = useState(false);
  const [showConfirmation, setShowConfirmation] = useState(false);
  const [error, setError] = useState<string>('');

  const submitApiRequest = (req: EditIngredientRequest) => {
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
  }

  const handleSave = () => {
    setOpen(false);

    submitApiRequest({id, name, categoryId: categoryId})
      .then((response) => checkResponse(response))
      .then(() => cleanUp())
      .then(() => onCompleted())
      .catch(handleError)
      .finally(() => setShowConfirmation(true));
  }

  const handleName = (event: React.ChangeEvent<HTMLInputElement>) => {
    setName((event.target as HTMLInputElement).value);
  };

  const handleCategoryId = (event: React.ChangeEvent<HTMLInputElement>) => {
    setCategoryId((event.target as HTMLInputElement).value);
  };

  const handleCloseEditDialog = () => {
    setOpen(false);
  };

  return (
    <>
      <IconButton aria-label="edit" onClick={() => setOpen(true)}>
        <Edit size="18"/>
      </IconButton>

      <Dialog
        open={open}
        onClose={onCompleted}
        aria-labelledby="simple-modal-title"
        aria-describedby="simple-modal-description"
      >
        <DialogTitle id="form-dialog-title">Edit ingredient '{name}'</DialogTitle>
        <DialogContent>
          <Box mt={2}>
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

            <TextField
              margin="dense"
              id="categoryId"
              label="Category"
              type="text"
              fullWidth
              onChange={handleCategoryId}
              value={categoryId}
            />
          </Box>
        </DialogContent>
        <DialogActions>
            <>
              <Button onClick={handleCloseEditDialog}>Close</Button>
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
