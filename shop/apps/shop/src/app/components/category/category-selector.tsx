import React, { useEffect, useState } from 'react';

import {
  Autocomplete,
  FormControl,
  InputLabel,
  TextField,
} from '@mui/material';
import { CategoryData } from 'service';

export interface CategorySelectorProps {
  value: string;
  options: CategoryData[];
  onChange: (categoryId: string) => void;
}

export const CategorySelector = ({
  value,
  options,
  onChange,
}: CategorySelectorProps) => {
  const [_, setLocalValue] = useState(value ?? '');
  useEffect(() => setLocalValue(value ?? ''), [value]);

  const handleChange = (option: string) => {
    if (onChange) {
      onChange(option);
    }
  };

  const getSelectedValue = (id: string) => {
    return listOptions.find((o) => o.id === id);
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
        defaultValue={getSelectedValue(value)}
      />
    </FormControl>
  );
};

export default CategorySelector;
