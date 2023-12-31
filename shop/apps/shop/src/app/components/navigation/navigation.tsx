import { Route, Routes, useNavigate } from 'react-router-dom';
import { AppBar, Box, Container, Menu, Toolbar } from '@mui/material';
import MuiMenuItem from '@mui/material/MenuItem';
import { useState } from 'react';
import DehazeIcon from '@mui/icons-material/Dehaze';
import ShoppingList from '../../pages/shopping-list';
import Ingredients from '../../pages/ingredients';
import Categories from '../../pages/categories';
import { Recipe } from '../recipe/recipe';
import Recipes from '../../pages/recipes';
import Menus from '../../pages/menus';

export interface RouteDefinition {
  path: string;
  text: string;
}

export interface RouteDefinitions {
  [key: string]: RouteDefinition;
}

// TODO: can we get this from home.tsx? or the other way around?
export const routes: RouteDefinitions = {
  ingredients: {
    path: '/ingredients',
    text: 'ingredients',
  },
  categories: {
    path: '/categories',
    text: 'categories',
  },
  recipes: {
    path: '/recipes',
    text: 'recipes',
  },
  menus: {
    path: '/menus',
    text: 'menus',
  },
  shoppingList: {
    path: '/shopping-list',
    text: 'shopping list',
  },
};

/*
      <Route path="users">
        <Route path=":userId" element={<ProfilePage />} />
        <Route path="me" element={...} />
      </Route>
 */
// TODO: text when we've got enough space?

export const Navigation = () => {
  const navigate = useNavigate();

  const [anchorEl, setAnchorEl] = useState<null | HTMLElement>(null);
  const open = Boolean(anchorEl);
  const handleClick = (event: any) => {
    setAnchorEl(event.currentTarget);
  };
  const handleClose = () => {
    setAnchorEl(null);
  };

  const handleNavigate = (option: string) => {
    const path = routes[option];
    if (path) {
      navigate(path.path, { replace: true, state: { text: path.text } });
    } else {
      navigate('/', { replace: true });
    }
  };

  return (
    <AppBar position="static">
      <Routes>
        <Route path="/ingredients/*" element={<Ingredients />} />
        <Route path="/categories/*" element={<Categories />} />
        <Route path="/recipes/*" element={<Recipes />} />
        <Route path="/menus/*" element={<Menus />} />
        <Route path="/shopping-list/*">
          <Route path="shopping-list/:firstDay" element={<ShoppingList />} />
        </Route>
      </Routes>
      <Container>
        <Toolbar
          style={{ display: 'flex', justifyContent: 'left', padding: 0 }}
        >
          <Box mr={2}>
            <DehazeIcon
              onClick={handleClick}
              aria-controls={open ? 'main-menu' : undefined}
              aria-haspopup="true"
              aria-expanded={open ? 'true' : undefined}
            />
          </Box>
          <Menu
            anchorEl={anchorEl}
            id="main-menu"
            open={open}
            onClose={handleClose}
            onClick={handleClose}
            PaperProps={{
              elevation: 0,
              sx: {
                overflow: 'visible',
                filter: 'drop-shadow(0px 2px 8px rgba(0,0,0,0.32))',
                mt: 1.5,
                '& .MuiAvatar-root': {
                  width: 32,
                  height: 32,
                  ml: -0.5,
                  mr: 1,
                },
                '&:before': {
                  content: '""',
                  display: 'block',
                  position: 'absolute',
                  top: 0,
                  right: 14,
                  width: 10,
                  height: 10,
                  bgcolor: 'background.paper',
                  transform: 'translateY(-50%) rotate(45deg)',
                  zIndex: 0,
                },
              },
            }}
            transformOrigin={{ horizontal: 'right', vertical: 'top' }}
            anchorOrigin={{ horizontal: 'right', vertical: 'bottom' }}
          >
            <MuiMenuItem selected={true} onClick={() => handleNavigate('home')}>
              Home
            </MuiMenuItem>
            <MuiMenuItem
              selected={true}
              onClick={() => handleNavigate('categories')}
            >
              Categories
            </MuiMenuItem>
            <MuiMenuItem
              selected={false}
              onClick={() => handleNavigate('ingredients')}
            >
              Ingredients
            </MuiMenuItem>
            <MuiMenuItem
              selected={false}
              onClick={() => handleNavigate('recipes')}
            >
              Recipes
            </MuiMenuItem>
            <MuiMenuItem
              selected={false}
              onClick={() => handleNavigate('menus')}
            >
              Menus
            </MuiMenuItem>
          </Menu>
        </Toolbar>
      </Container>
    </AppBar>
  );
};
