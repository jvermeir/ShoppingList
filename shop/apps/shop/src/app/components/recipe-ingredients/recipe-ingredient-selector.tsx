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

export default RecipeIngredientSelector;
