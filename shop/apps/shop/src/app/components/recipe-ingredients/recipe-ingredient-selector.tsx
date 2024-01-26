import React, { useEffect, useState } from 'react';

import {
  Autocomplete,
  FormControl,
  InputLabel,
  TextField,
} from '@mui/material';
import { IngredientData } from 'service';

export interface RecipeIngredientSelectorProps {
  value: string;
  options: IngredientData[];
  onChange: (ingredientId: string) => void;
}

export const RecipeIngredientSelector = ({
  value,
  options,
  onChange,
}: RecipeIngredientSelectorProps) => {
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
        renderInput={(params) => <TextField {...params} label="Ingredient" />}
        onChange={(e, v) => {
          v && handleChange(v.id);
        }}
        isOptionEqualToValue={(option, value) => option.id === value.id}
        defaultValue={getSelectedValue(value)}
      />
    </FormControl>
  );
};

export default RecipeIngredientSelector;
