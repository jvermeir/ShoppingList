import { Container, Grid, Typography } from '@mui/material';
import { Navigation } from '../components/navigation/navigation';
import BasicCard from '../components/navigation/basic-card';
import * as React from 'react';

export const HomePage = () => {
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
          <Grid item>
            <BasicCard
              title="cookbook"
              text="All recipes"
              link="/recipes"
              image="assets/cookbook.webp"
            />
          </Grid>
          <Grid item>
            <BasicCard
              title="menus"
              text="All menus"
              link="/menus"
              image="assets/menu.webp"
            />
          </Grid>
        </Grid>
      </Container>
    </>
  );
};

export default HomePage;
