import {Container, Typography,} from '@mui/material';
import {Navigation} from "../components/navigation/navigation";

// TODO: test

export interface CategoryData {
  id: string;
  name: string;
  shopOrder: number;
}

export const HelloPage = () => {
  return (
    <>
      <Navigation/>
      <Container>
        <Typography color="textPrimary" mt={3}>
          Hello! what are you doing here, then?
        </Typography>
      </Container>
    </>
  );
};

export default HelloPage;
