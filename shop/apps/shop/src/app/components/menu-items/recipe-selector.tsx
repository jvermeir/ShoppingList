import React, { useEffect, useState } from 'react';

import {
  Autocomplete,
  FormControl,
  InputLabel,
  MenuItem,
  Select,
  SelectChangeEvent,
  TextField,
} from '@mui/material';
import { RecipeData } from '../../pages/recipes';

// TODO: add type-to-search when selecting a recipe

export interface RecipeSelectorProps {
  name?: string;
  value: string;
  options: RecipeData[];
  onChange: (recipeId: string) => void;
}

export const RecipeSelector = ({
  name,
  value,
  options,
  onChange,
}: RecipeSelectorProps) => {
  const [localValue, setLocalValue] = useState(value ?? '');
  useEffect(() => setLocalValue(value ?? ''), [value]);

  const handleChange = (option: string) => {
    if (onChange) {
      onChange(option);
    }
  };

  const listOptions = options.map((option) => {
    return {
      label: option.name,
      id: option.id,
      name: option.name,
    };
  });

  return (
    <FormControl fullWidth>
      <InputLabel />
      <Autocomplete
        disablePortal
        id="combo-box-demo"
        options={listOptions}
        sx={{ width: 300 }}
        renderInput={(params) => <TextField {...params} label="Ingredient" />}
        onChange={(e, v) => {
          v && handleChange(v.id);
        }}
        isOptionEqualToValue={(option, value) => option.id === value.id}
      />
    </FormControl>
  );
};

export default RecipeSelector;
