import "./App.css";
import HomePage from "./Pages/Home_page/Home_page";
import { BrowserRouter as Router, Route, Routes} from "react-router-dom";
import { PizzaForm, PizzaListPage } from "./utils";
import NavBar from "./Camponents/NavBar/NavBar";
import GetUsersData from "./Pages/Get users data/GetUsersData";
import IngredientsPage from "./Pages/IngredienyPage/IngredientPage";
import RegisterPage from "./Pages/RegisterPage/RegisterPage";
import OrderProcesingPage from "./Pages/OrderProcesingPage/OrderProcesingPage";
import OrderDetailsPage from "./Pages/OrderDetailsPagge/OrderDetailsPage";


function App() {



  return (

    <Router>
      <NavBar></NavBar>
      <Routes>
        <Route path="/" element={<HomePage />} /> 
        <Route path="/standart-pizza-list" element={<PizzaListPage />} /> 
        <Route path="/usersData" element={<GetUsersData/>}/>
        <Route path= "/ingredients-list" element={<IngredientsPage/>}/>
        <Route path= '/register' element={<RegisterPage></RegisterPage>}/>
        <Route path= '/add-pizza' element={<PizzaForm></PizzaForm>}/>
        <Route path= '/admin-orders' element={<OrderProcesingPage></OrderProcesingPage>}/>
        <Route path= '/order-details' element={<OrderDetailsPage></OrderDetailsPage>}/>
      </Routes>
    </Router>
  )
}

export default App;
