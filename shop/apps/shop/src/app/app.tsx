import { Route, Routes } from 'react-router';
import CategoriesPage from './pages/categories';
import IngredientsPage from './pages/ingredients';
import RecipesPage from './pages/recipes';
import HomePage from './pages/home';

// TODO: how is this connected to routes in Navigation?

export function App() {
  return (
    <Routes>
      <Route path="/" element={<HomePage />} />
      <Route path="/categories" element={<CategoriesPage />} />
      <Route path="/ingredients" element={<IngredientsPage />} />
      <Route path="/recipes" element={<RecipesPage />} />
    </Routes>
  );
}

export default App;
