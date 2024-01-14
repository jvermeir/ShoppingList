import React, { useEffect, useState } from 'react';

import {
  FormControl,
  InputLabel,
  MenuItem,
  Select,
  SelectChangeEvent,
} from '@mui/material';
import { IngredientData } from 'service';

export interface RecipeIngredientSelectorProps {
  name?: string;
  value: string;
  options: IngredientData[];
  onChange: (ingredientId: string) => void;
}

export const RecipeIngredientSelector = ({
  name,
  value,
  options,
  onChange,
}: RecipeIngredientSelectorProps) => {
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
      <InputLabel>Ingredient</InputLabel>
      <Select
        name={name}
        value={localValue}
        label="Ingredient"
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

export default RecipeIngredientSelector;
