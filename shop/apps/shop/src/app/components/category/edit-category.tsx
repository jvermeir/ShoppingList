import {
  Box,
  Button,
  CircularProgress,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  IconButton,
  TextField
} from "@mui/material";
import {Edit} from "react-feather";
import {CategoryData} from "../../pages/categories";
import {useState} from "react";

export interface EditCategoryProps {
  category: CategoryData,
  onCompleted?: () => void
}
// TODO: handle loading and error

export const EditCategory = ({category, onCompleted}:EditCategoryProps) => {
  const [id, setId] = useState<string>(category.id || '')
  const [name, setName] = useState<string>(category.name || '')
  const [shopOrder, setShopOrder] = useState<number>(category.shopOrder || 0)
  const [open, setOpen] = useState(false);

  const [done, setDone] = useState<boolean>(false);
  const [loading, setLoading] = useState<boolean>(false);
  const [error, setError] = useState<string>('');

  const handleSave = () => {
    setOpen(false);

    fetch('/api/category', {
      method: 'POST',
      headers: {
        "Content-type": "application/json; charset=UTF-8"
      },
      body: JSON.stringify({
        id: id,
        name: name,
        shopOrder: shopOrder
      }),
    }).then(_ => {
      onCompleted && onCompleted()
    });
  }

  const handleName = (event: React.ChangeEvent<HTMLInputElement>) => {
    setName((event.target as HTMLInputElement).value);
  };

  const handleShopOrder = (event: React.ChangeEvent<HTMLInputElement>) => {
    const number = parseInt((event.target as HTMLInputElement).value);
    setShopOrder(isNaN(number) ? 0 : number);
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
          <DialogTitle id="form-dialog-title">Edit category {name}</DialogTitle>
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
            {loading  ? (
              <Button color="primary">
                <CircularProgress
                  size={24}
                  sx={{
                    color: 'white',
                  }}
                />
              </Button>
            ) : (
              <>
                <Button onClick={handleCloseEditDialog}>Close</Button>
                <Button variant="contained" onClick={handleSave}>
                  Save
                </Button>
              </>
            )}
          </DialogActions>
        </Dialog>
    </>

  );
}
