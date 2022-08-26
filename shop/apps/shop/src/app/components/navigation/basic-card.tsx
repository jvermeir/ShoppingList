import * as React from 'react';
import Box from '@mui/material/Box';
import Card from '@mui/material/Card';
import CardContent from '@mui/material/CardContent';
import Typography from '@mui/material/Typography';
import CardMedia from '@mui/material/CardMedia';
import { Button } from '@mui/material';
import { useNavigate } from 'react-router-dom';

export default function BasicCard({
  title,
  text,
  link,
  image,
}: {
  title: string;
  text: string;
  link: string;
  image: string;
}) {
  const navigate = useNavigate();

  return (
    <Card sx={{ display: 'flex', width: '100%', mt: 1, mb: 1 }}>
      <Box sx={{ display: 'flex', flexDirection: 'column' }}>
        <CardContent sx={{ flex: '1 0 auto' }}>
          <Typography
            sx={{
              ml: 0.5,
              variant: 'subtitle1',
              color: 'text.secondary',
              component: 'div',
            }}
          >
            {text}
          </Typography>
          <Button size="small" onClick={() => navigate(link)}>
            {title}
          </Button>
        </CardContent>
      </Box>
      <CardMedia
        component="img"
        sx={{ width: 151, height: 92 }}
        image={image}
        alt={text}
      />
    </Card>
  );
}
