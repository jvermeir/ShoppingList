import { IconButton, TableRow } from '@mui/material';
import { Delete } from 'react-feather';
import { EditIngredient } from './edit-ingredient';
import StyledTableCell from '../styled-components/StyledTableCell';
import { CategoryData, IngredientData } from 'service';

export const Ingredient = ({
  ingredient,
  categories,
  onCompleted,
}: {
  ingredient: IngredientData;
  categories: CategoryData[];
  onCompleted: () => void;
}) => {
  const handleDelete = () => {
    fetch(`/api/ingredient/${ingredient.id}`, {
      method: 'DELETE',
    }).then((_) => onCompleted && onCompleted());
  };

  return (
    <TableRow key={ingredient.id} hover={true}>
      <StyledTableCell>{ingredient.name}</StyledTableCell>
      <StyledTableCell>{ingredient.categoryName}</StyledTableCell>
      <StyledTableCell>{ingredient.unit}</StyledTableCell>
      <StyledTableCell>
        <EditIngredient
          ingredient={ingredient}
          categories={categories}
          onCompleted={onCompleted}
        />
        <IconButton aria-label="delete" onClick={handleDelete}>
          <Delete size="18" />
        </IconButton>
      </StyledTableCell>
    </TableRow>
  );
};
