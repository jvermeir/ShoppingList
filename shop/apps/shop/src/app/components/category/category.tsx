import {IconButton, TableCell, TableRow} from "@mui/material";
import {Delete} from "react-feather";
import {CategoryData} from "../../pages/categories";
import {useState} from "react";
import {EditCategory} from "./edit-category";

// TODO: handle loading and error
// TODO: set* aren't used, can we remove 'm?

export const Category = ({category, onCompleted}: { category: CategoryData, onCompleted: () => void }) => {
  const [id, setId] = useState<string>(category.id || '')
  const [name, setName] = useState<string>(category.name || '')
  const [shopOrder, setShopOrder] = useState<number>(category.shopOrder || 0)

  function handleDelete() {
    fetch(`/api/category/${id}`, {
      method: 'DELETE',
    }).then(_ => onCompleted && onCompleted())
  }

  return (
    <TableRow key={id} hover={true}>
      <TableCell>{name}</TableCell>
      <TableCell>{shopOrder}</TableCell>
      <TableCell>
        <EditCategory category={category} onCompleted={onCompleted}/>
        <IconButton aria-label="delete" onClick={handleDelete}>
          <Delete size="18"/>
        </IconButton>
      </TableCell>
    </TableRow>
  );
}
