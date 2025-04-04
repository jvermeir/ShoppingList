import React, { useEffect, useState } from 'react';

import {
  Autocomplete,
  FormControl,
  InputLabel,
  TextField,
} from '@mui/material';
import { RecipeData } from '../../pages/recipes';

// TODO: add type-to-search when selecting a recipe

export interface RecipeSelectorProps {
  value: string;
  options: RecipeData[];
  onChange: (recipeId: string) => void;
}

export const RecipeSelector = ({
  value,
  options,
  onChange,
}: RecipeSelectorProps) => {
  const [_, setLocalValue] = useState(value ?? '');
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

  const getSelectedValue = (id: string) => {
    return listOptions.find((o) => o.id === id);
  };

  return (
    <FormControl fullWidth>
      <InputLabel />
      <Autocomplete
        disablePortal
        id="combo-box-demo"
        options={listOptions}
        sx={{ width: 300 }}
        renderInput={(params) => <TextField {...params} label="Recipe" />}
        onChange={(e, v) => {
          v && handleChange(v.id);
        }}
        isOptionEqualToValue={(option, value) => option.id === value.id}
        defaultValue={getSelectedValue(value)}
      />
    </FormControl>
  );
};

export default RecipeSelector;
