import {IconButton, TableCell, TableRow} from "@mui/material";
import {Delete} from "react-feather";
import {IngredientData} from "../../pages/ingredients";
import {EditIngredient} from "./edit-ingredient";
import {CategoryData} from "../../pages/categories";

export const Ingredient = ({ingredient, categories, onCompleted}: { ingredient: IngredientData, categories:CategoryData[], onCompleted: () => void }) => {

  const  handleDelete = () => {
    fetch(`/api/ingredient/${ingredient.id}`, {
      method: 'DELETE',
    }).then(_ => onCompleted && onCompleted())
  }

  return (
    <TableRow key={ingredient.id} hover={true}>
      <TableCell>{ingredient.name}</TableCell>
      <TableCell>{ingredient.categoryName}</TableCell>
      <TableCell>
        <EditIngredient ingredient={ingredient} categories={categories} onCompleted={onCompleted}/>
        <IconButton aria-label="delete" onClick={handleDelete}>
          <Delete size="18"/>
        </IconButton>
      </TableCell>
    </TableRow>
  );
}
