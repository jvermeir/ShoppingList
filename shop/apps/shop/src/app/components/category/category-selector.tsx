import React, { useEffect, useState } from 'react';

import {
  Autocomplete,
  FormControl,
  InputLabel,
  TextField,
} from '@mui/material';
import { CategoryData } from 'service';

export interface CategorySelectorProps {
  name?: string;
  value: string;
  options: CategoryData[];
  onChange: (categoryId: string) => void;
}

type ListOption = {
  label: string;
  shopOrder: number;
  id: string;
  name: string;
};

export const CategorySelector = ({
  name,
  value,
  options,
  onChange,
}: CategorySelectorProps) => {
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
      shopOrder: option.shopOrder,
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
        renderInput={(params) => <TextField {...params} label="Category" />}
        onChange={(e, v) => {
          v && handleChange(v.id);
        }}
        isOptionEqualToValue={(option, value) => option.id === value.id}
      />
    </FormControl>
  );
};

export default CategorySelector;
