import {
  Alert,
  Box,
  Paper,
  Snackbar,
  Table,
  TableBody,
  TableContainer,
} from '@mui/material';
import React, { useState } from 'react';
import { OutputShoppingListCategory } from '../../../schema/output-schema';
import { ShoppingListIngredient } from './shopping-list-ingredient';

export interface EditShoppingListCategoryProps {
  shoppingListId: string;
  category: OutputShoppingListCategory;
  onCompleted: () => void;
}

export const EditShoppingListCategory = ({
  shoppingListId,
  category,
  onCompleted,
}: EditShoppingListCategoryProps) => {
  const [showConfirmation, setShowConfirmation] = useState(false);
  const [error, _] = useState<string>('');

  return (
    <>
      <Box>
        {category.ingredients && category.ingredients.length > 0 && (
          <Box ml={-3} mr={-5}>
            <TableContainer component={Paper}>
              <Table aria-label="simple table">
                <TableBody>
                  {category.ingredients
                    .sort((a, b) => {
                      if (a?.name === b?.name || !a?.name || !b?.name) return 0;
                      return a.name < b.name ? -1 : 1;
                    })
                    .filter((ingredient) => !!ingredient)
                    .map((ingredient) => (
                      <ShoppingListIngredient
                        key={ingredient.id}
                        shoppingListId={shoppingListId}
                        ingredient={ingredient}
                        onCompleted={onCompleted}
                      />
                    ))}
                </TableBody>
              </Table>
            </TableContainer>
          </Box>
        )}
      </Box>

      <Snackbar
        open={showConfirmation}
        autoHideDuration={4000}
        onClose={() => setShowConfirmation(false)}
      >
        <Alert severity={error ? 'error' : 'success'}>
          {error ? error : 'Done'}
        </Alert>
      </Snackbar>
    </>
  );
};
