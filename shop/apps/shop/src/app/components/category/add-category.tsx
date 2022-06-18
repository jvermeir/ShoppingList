import {
  Box,
  Button,
  Dialog,
  DialogActions,
  DialogContent,
  DialogContentText,
  DialogTitle,
  TextField
} from "@mui/material";
import {useState} from "react";
import {Plus} from "react-feather";

export interface CreateCategoryProps {
  onCompleted?: () => void;
}

// TODO: handle loading and error
// TODO: naming
// TODO: parameter to add vs. edit?
// TODO: why does this form show previous values?

export const AddCategory = ({onCompleted}:CreateCategoryProps) => {
  const [name, setName] = useState<string>('')
  const [shopOrder, setShopOrder] = useState<number>(0)
  const [open, setOpen] = useState(false);

  const [done, setDone] = useState<boolean>(false);
  const [showAddDialog, setShowAddDialog] = useState<boolean>(true);

  const handleSave = () => {
    // TODO: handle errors
    setOpen(false);

    fetch('/api/category', {
      method: 'POST',
      headers: {
        "Content-type": "application/json; charset=UTF-8"
      },
      body: JSON.stringify({
        name: name,
        shopOrder: shopOrder
      }),
    }).then(_ => onCompleted && onCompleted());
  }

  const handleName = (event: React.ChangeEvent<HTMLInputElement>) => {
    setName((event.target as HTMLInputElement).value);
  };

  const handleShopOrder = (event: React.ChangeEvent<HTMLInputElement>) => {
    const number = parseInt((event.target as HTMLInputElement).value);
    setShopOrder(isNaN(number) ? 0 : number);
  };

  const handleCloseADdDialog = () => {
    setShowAddDialog(false);
  };

  return (
    <>
    <Button variant="contained" sx={{ float: 'right' }} onClick={() => setOpen(true)} startIcon={<Plus />}>
      Add Category
    </Button>
    <Dialog
      open={open}
      onClose={onCompleted}
      aria-labelledby="simple-modal-title"
      aria-describedby="simple-modal-description"
    >
      <DialogTitle id="form-dialog-title">Category {name}</DialogTitle>
      <DialogContent>
        <DialogContentText>Add Category</DialogContentText>
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
            autoFocus
            margin="dense"
            id="shopOrder"
            label="Shop order"
            type="number"
            fullWidth
            onChange={handleShopOrder}
            value={shopOrder}
          />
        </Box>
      </DialogContent>
      <DialogActions>
        <>
          <Button onClick={handleCloseADdDialog}>Close</Button>
          <Button variant="contained" onClick={handleSave}>
            Save
          </Button>
        </>
      </DialogActions>
    </Dialog>
    </>
  );
}
