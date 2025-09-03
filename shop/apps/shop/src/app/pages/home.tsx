import { Container, Box, Typography } from '@mui/material';
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
        <Box
          sx={{
            display: 'flex',
            flexWrap: 'wrap',
            gap: { xs: 2, sm: 3 },
            justifyContent: { xs: 'center', sm: 'flex-start' }
          }}
        >
          <Box sx={{ flex: { xs: '1 1 100%', sm: '1 1 45%', md: '1 1 22%' } }}>
            <BasicCard
              title="categories"
              text="All about categories"
              link="/categories"
              image="assets/categories.webp"
            />
          </Box>
          <Box sx={{ flex: { xs: '1 1 100%', sm: '1 1 45%', md: '1 1 22%' } }}>
            <BasicCard
              title="ingredients"
              text="All about ingredients"
              link="/ingredients"
              image="assets/ingredients.webp"
            />
          </Box>
          <Box sx={{ flex: { xs: '1 1 100%', sm: '1 1 45%', md: '1 1 22%' } }}>
            <BasicCard
              title="cookbook"
              text="All recipes"
              link="/recipes"
              image="assets/cookbook.webp"
            />
          </Box>
          <Box sx={{ flex: { xs: '1 1 100%', sm: '1 1 45%', md: '1 1 22%' } }}>
            <BasicCard
              title="menus"
              text="All menus"
              link="/menus"
              image="assets/menu.webp"
            />
          </Box>
        </Box>
      </Container>
    </>
  );
};

export default HomePage;
