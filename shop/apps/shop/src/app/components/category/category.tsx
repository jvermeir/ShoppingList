import { IconButton, TableRow } from '@mui/material';
import { Delete } from 'react-feather';
import { CategoryData } from '../../pages/categories';
import { EditCategory } from './edit-category';
import StyledTableCell from '../styled-components/StyledTableCell';

export const Category = ({
  category,
  onCompleted,
}: {
  category: CategoryData;
  onCompleted: () => void;
}) => {
  const handleDelete = () => {
    fetch(`/api/category/${category.id}`, {
      method: 'DELETE',
    }).then((_) => onCompleted && onCompleted());
  };

  return (
    <TableRow key={category.id} hover={true}>
      <StyledTableCell>{category.name}</StyledTableCell>
      <StyledTableCell>{category.shopOrder}</StyledTableCell>
      <StyledTableCell>
        <EditCategory category={category} onCompleted={onCompleted} />
        <IconButton aria-label="delete" onClick={handleDelete}>
          <Delete size="18" />
        </IconButton>
      </StyledTableCell>
    </TableRow>
  );
};
