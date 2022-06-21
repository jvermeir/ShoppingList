import {Box, Button, Dialog, DialogActions, DialogContent, DialogTitle, TextField} from "@mui/material";
import {useState} from "react";
import {Plus} from "react-feather";

export interface CreateCategoryProps {
  onCompleted?: () => void;
}

// TODO: handle loading and error
// TODO: naming
// TODO: reuse EditCategory code? parameter to add vs. edit?
// TODO: why does this form show previous values? unless set to empty/0 explicitly

export const AddCategory = ({onCompleted}:CreateCategoryProps) => {
  const [name, setName] = useState<string>('')
  const [shopOrder, setShopOrder] = useState<number>(0)
  const [open, setOpen] = useState(false);

  const [done, setDone] = useState<boolean>(false);

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
    }).then(_ => {
      onCompleted && onCompleted()
    });

    setName('');
    setShopOrder(0);
  }

  const handleName = (event: React.ChangeEvent<HTMLInputElement>) => {
    setName((event.target as HTMLInputElement).value);
  };

  const handleShopOrder = (event: React.ChangeEvent<HTMLInputElement>) => {
    const number = parseInt((event.target as HTMLInputElement).value);
    setShopOrder(isNaN(number) ? 0 : number);
  };

  const handleCloseAddDialog = () => {
    setOpen(  false);
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
      <DialogTitle id="form-dialog-title">Add Category</DialogTitle>
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
          <Button onClick={handleCloseAddDialog}>Close</Button>
          <Button variant="contained" onClick={handleSave}>
            Save
          </Button>
        </>
      </DialogActions>
    </Dialog>
    </>
  );
}
