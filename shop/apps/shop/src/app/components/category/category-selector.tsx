import React, {useEffect, useState} from 'react';

import {FormControl, InputLabel, MenuItem, Select, SelectChangeEvent} from "@mui/material";
import {CategoryData} from "../../pages/categories";

export interface CategorySelectorProps {
  name?: string,
  value: string,
  options: CategoryData[]
  onChange: (categoryId: string) => void
}

export const CategorySelector = ({name, value, options, onChange}: CategorySelectorProps) => {
  const [localValue, setLocalValue] = useState(value ?? '');
  useEffect(() => setLocalValue(value ?? ''), [value]);

  const handleChange = (event: SelectChangeEvent<string>) => {
    const value = event.target.value
    if (onChange) {
      onChange(value);
    }
  }

  return (
    <FormControl fullWidth>
      <InputLabel>Category</InputLabel>
      <Select
        name={name}
        value={localValue}
        label="Category"
        onChange={handleChange}
      >
        {options?.map(option => {
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

export default CategorySelector;
