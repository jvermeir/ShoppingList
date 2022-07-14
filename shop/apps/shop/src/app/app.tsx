import {BrowserRouter, Route, Routes} from "react-router-dom";
import CategoriesPage from "./pages/categories";
import HelloPage from "./pages/hello";
import IngredientsPage from "./pages/ingredients";
import {ReactElement} from "react";

export interface RouteDefinition {
  path: string;
  element: () => ReactElement | null;
}
export function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<HelloPage />}/>
        <Route path="/categories" element={<CategoriesPage />}/>
        <Route path="/ingredients" element={<IngredientsPage />}/>
      </Routes>
    </BrowserRouter>
  );
}
export default App;

// <BrowserRouter>
//   <Routes>
//     <Route path="/" element={<HelloPage />}/>
//     <Route path="/categories" element={<CategoriesPage />}/>
//     <Route path="/ingredients" element={<IngredientsPage />}/>
//   </Routes>
// </BrowserRouter>
