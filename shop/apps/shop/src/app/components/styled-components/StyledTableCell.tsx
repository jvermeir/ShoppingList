import { styled, TableCell } from '@mui/material';
import theme from '../../../theme';

const StyledTableCell = styled(TableCell)({
  [theme.breakpoints.down('sm')]: {
    paddingTop: 0,
    paddingBottom: 0,
  },
  [theme.breakpoints.up('sm')]: {
    paddingTop: '1rem',
    paddingBottom: '1rem',
  },
});

export default StyledTableCell;
