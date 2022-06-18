import { Box, CircularProgress, Typography } from '@mui/material';

export const Loading = () => (
  <Box
    sx={{
      display: 'flex',
      flexDirection: 'column',
      justifyContent: 'center',
      alignContent: 'center',
      alignItems: 'center',
    }}
    mt={10}
  >
    <CircularProgress color="primary" size={50} />
    <Typography
      color="textPrimary"
      variant="h2"
      sx={{
        display: 'flex',
        justifyContent: 'center',
        my: '40px',
      }}
    >
      Bezig met laden...
    </Typography>
  </Box>
);
