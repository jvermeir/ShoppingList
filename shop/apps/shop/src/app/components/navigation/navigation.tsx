import {useNavigate} from 'react-router-dom';
import {User as UserIcon} from 'react-feather';
import {AppBar, Box, Container, Divider, Menu, Toolbar} from '@mui/material';
import MuiMenuItem from '@mui/material/MenuItem';
import {useState} from 'react';

export interface RouteDefinition {
  path: string;
  text: string;
}

export interface RouteDefinitions {
  [key: string]: RouteDefinition;
}

const routes: RouteDefinitions = {
  ingredients: {
    path: '/ingredients',
    text: 'ingredients'
  },
  categories: {
    path: '/categories',
    text: 'categories'
  },
};

// TODO: icon
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
      navigate(path.path, {replace: true});
    } else {
      navigate("/", {replace: true});
    }
  }

  return (
    <AppBar position="static">
      <Container>
        <Toolbar style={{display: 'flex', justifyContent: 'center', padding: 0}}>

          <div
            style={{
              marginLeft: 'auto',
              display: 'flex',
              alignItems: 'center',
            }}
          >
            <Box mr={2}>
              <UserIcon
                onClick={handleClick}
                aria-controls={open ? 'account-menu' : undefined}
                aria-haspopup="true"
                aria-expanded={open ? 'true' : undefined}
              />
            </Box>
            <Menu
              anchorEl={anchorEl}
              id="account-menu"
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
              transformOrigin={{horizontal: 'right', vertical: 'top'}}
              anchorOrigin={{horizontal: 'right', vertical: 'bottom'}}
            >
              <MuiMenuItem disabled={true}>Menu</MuiMenuItem>
              <Divider/>
              <MuiMenuItem selected={true} onClick={() => handleNavigate("categories")}>
                Categories
              </MuiMenuItem>
              <MuiMenuItem selected={false} onClick={() => handleNavigate("ingredients")}>
                Ingredients
              </MuiMenuItem>
            </Menu>
          </div>
        </Toolbar>
      </Container>
    </AppBar>
  );
};
