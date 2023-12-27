import React, { useEffect, useState } from 'react';

import {
  FormControl,
  InputLabel,
  MenuItem,
  Select,
  SelectChangeEvent,
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

  const handleChange = (event: SelectChangeEvent<string>) => {
    const value = event.target.value;
    if (onChange) {
      onChange(value);
    }
  };

  return (
    <FormControl fullWidth>
      <InputLabel>Recipe</InputLabel>
      <Select
        name={name}
        value={localValue}
        label="Recipe"
        onChange={handleChange}
      >
        {options?.map((option) => {
          return (
            <MenuItem key={option.id} value={option.id}>
              {option.name}
            </MenuItem>
          );
        })}
      </Select>
    </FormControl>
  );
};

export default RecipeSelector;
