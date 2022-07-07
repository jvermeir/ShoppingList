import { createTheme, responsiveFontSizes } from '@mui/material/styles';

// A custom theme for this app
const theme = createTheme();

const responsiveTheme = responsiveFontSizes(theme);

export default responsiveTheme;
