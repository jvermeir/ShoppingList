import {Container, Typography,} from '@mui/material';

// TODO: test

export interface CategoryData {
  id: string;
  name: string;
  shopOrder: number;
}

export const HelloPage = () => {
  return (
    <Container>
          <Typography color="textPrimary" mt={3}>
            Hello! you
          </Typography>
      </Container>
  );
};

export default HelloPage;
