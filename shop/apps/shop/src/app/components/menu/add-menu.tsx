import {
  Alert,
  Box,
  Button,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  Snackbar,
  TextField,
} from '@mui/material';
import { DatePicker } from '@mui/x-date-pickers/DatePicker';
import React, { useEffect, useState } from 'react';
import { Plus } from 'react-feather';
import { HttpError } from '../error/error';
import { RecipeData } from '../../pages/recipes';
import { createMenu } from 'service';
import { LocalizationProvider } from '@mui/x-date-pickers/LocalizationProvider';
import { AdapterDateFns } from '@mui/x-date-pickers/AdapterDateFns';
import { nl } from 'date-fns/locale/nl';

/*
TODO: this page doesn't allow adding new recipe ingredients. should it?
TODO: when cursor is in ingredient, the text `ingredient` is partly obscured
TODO: if unit is liter or kilogram, then amount prompt should be 'amount', if not, it should be 'number'?
 */
export interface AddMenuProps {
  recipes: RecipeData[];
  onCompleted: () => void;
}

export const AddMenu = ({ onCompleted }: AddMenuProps) => {
  const [firstDay, setFirstDay] = useState<Date>(new Date());
  const [open, setOpen] = useState(false);
  const [showConfirmation, setShowConfirmation] = useState(false);
  const [error, setError] = useState<string>('');
  const [value, setValue] = React.useState(new Date());

  const handleError = (error: HttpError) =>
    error.code === 409
      ? setError('Duplicate recipe name')
      : setError(`${error.code}: ${error.message}`);

  useEffect(() => {
    setFirstDay(new Date());
  }, []);

  const checkResponse = (response: Response) => {
    if (!response.ok) {
      throw new HttpError(response.status, response.statusText);
    }
  };

  const cleanUp = () => {
    setOpen(false);
    setFirstDay(new Date());
  };

  const handleSave = () => {
    setShowConfirmation(false);
    setError('');

    createMenu({ firstDay: firstDay.toISOString().split('T')[0] })
      .then((response) => checkResponse(response))
      .then(() => cleanUp())
      .then(() => onCompleted())
      .catch(handleError)
      .finally(() => setShowConfirmation(true));
  };

  const handleFirstDay = (date: Date) => {
    setFirstDay(date);
  };

  const handleDateChange = (date: Date) => {
    console.log(date);
    setValue(date);
  };

  return (
    <LocalizationProvider dateAdapter={AdapterDateFns} adapterLocale={nl}>
      <Button
        variant="contained"
        sx={{ float: 'right' }}
        onClick={() => setOpen(true)}
        startIcon={<Plus />}
      >
        <Box sx={{ display: { xs: 'none', sm: 'block' } }}>Add Menu</Box>
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
          Add menu starting on {firstDay.toDateString()}
        </DialogTitle>

        <DialogContent sx={{ mb: { xs: -3, md: 1 }, mt: { xs: 0 } }}>
          <Box sx={{ mb: { xs: 0, md: 1 } }}>
            <DatePicker
              label="Controlled picker"
              value={firstDay}
              onChange={(value) => handleFirstDay(value || new Date())}
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
    </LocalizationProvider>
  );
};
