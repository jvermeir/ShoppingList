import {BrowserRouter, Route, Routes} from "react-router-dom";
import './theme/index.css';
import CategoriesPage from "./pages/categories";

export function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<CategoriesPage />}>
        </Route>
      </Routes>
    </BrowserRouter>
  );
}

export default App;
