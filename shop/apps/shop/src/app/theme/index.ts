import { colors } from '@mui/material';
import { createTheme } from '@mui/material/styles';
import shadows from './shadows';
import typography from './typography';

const theme = createTheme({
  palette: {
    background: {
      default: '#F4F6F8',
      paper: colors.common.white,
    },
    primary: {
      contrastText: '#ffffff',
      main: '#e30027',
    },
    secondary: {
      contrastText: '#ffffff',
      main: '#ffffff',
    },
    text: {
      primary: '#172b4d',
      secondary: '#6b778c',
    },
  },
  shadows,
  typography,
  components: {
    MuiDialogTitle: {
      styleOverrides: {
        root: {
          fontSize: '1.6em',
        },
      },
    },
  },
});

export default theme;
