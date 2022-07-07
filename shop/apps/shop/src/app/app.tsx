import {BrowserRouter, Route, Routes} from "react-router-dom";
import CategoriesPage from "./pages/categories";
import HelloPage from "./pages/hello";

export function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<HelloPage />}/>
        <Route path="/categories" element={<CategoriesPage />}/>
      </Routes>
    </BrowserRouter>
  );
}

export default App;
