import {IconButton, TableCell, TableRow} from "@mui/material";
import {Delete} from "react-feather";
import {CategoryData} from "../../pages/categories";
import {EditCategory} from "./edit-category";

// TODO: handle loading and error

export const Category = ({category, onCompleted}: { category: CategoryData, onCompleted: () => void }) => {

  const  handleDelete = () => {
    fetch(`/api/category/${category.id}`, {
      method: 'DELETE',
    }).then(_ => onCompleted && onCompleted())
  }

  return (
    <TableRow key={category.id} hover={true}>
      <TableCell>{category.name}</TableCell>
      <TableCell>{category.shopOrder}</TableCell>
      <TableCell>
        <EditCategory category={category} onCompleted={onCompleted}/>
        <IconButton aria-label="delete" onClick={handleDelete}>
          <Delete size="18"/>
        </IconButton>
      </TableCell>
    </TableRow>
  );
}
