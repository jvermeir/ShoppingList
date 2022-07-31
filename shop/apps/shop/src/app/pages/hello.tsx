import { Container, Grid, Typography } from '@mui/material';
import { Navigation } from '../components/navigation/navigation';
import BasicCard from '../components/navigation/basic-card';
import * as React from 'react';

// TODO: test

export interface CategoryData {
  id: string;
  name: string;
  shopOrder: number;
}

export const HelloPage = () => {
  return (
    <>
      <Navigation />
      <Container>
        <Typography color="textPrimary" mt={3} mb={3}>
          Hello! what are you doing here, then?
        </Typography>
        <Grid container spacing={{ sm: 2, md: 3 }} columns={{ xs: 12 }}>
          <Grid item>
            <BasicCard
              title="categories"
              text="All about categories"
              link="/categories"
              image="assets/categories.webp"
            />
          </Grid>
          <Grid item>
            <BasicCard
              title="ingredients"
              text="All about ingredients"
              link="/ingredients"
              image="assets/ingredients.webp"
            />
          </Grid>
        </Grid>
      </Container>
    </>
  );
};

export default HelloPage;
