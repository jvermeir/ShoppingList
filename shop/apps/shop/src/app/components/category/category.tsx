import {IconButton, TableCell, TableRow} from "@mui/material";
import {Delete} from "react-feather";
import {CategoryData} from "../../pages/categories";
import {EditCategory} from "./edit-category";

export const Category = ({category, onCompleted}: { category: CategoryData, onCompleted: () => void }) => {

  const  handleDelete = () => {
    fetch(`/api/category/${category.id}`, {
      method: 'DELETE',
    }).then(_ => onCompleted && onCompleted())
  }

  return (
    <TableRow key={category.id} hover={true}>
      <TableCell sx={{paddingTop: {xs:0, sm:2}, paddingBottom: {xs:0, sm:2}}}>{category.name}</TableCell>
      <TableCell sx={{paddingTop: {xs:0, sm:2}, paddingRight: {xs:0, sm:2}, paddingBottom: {xs:0}}}>{category.shopOrder}</TableCell>
      <TableCell sx={{padding: {xs:0, sm:2}}}>
        <EditCategory category={category} onCompleted={onCompleted}/>
        <IconButton aria-label="delete" onClick={handleDelete}>
          <Delete size="18"/>
        </IconButton>
      </TableCell>
    </TableRow>
  );
}
