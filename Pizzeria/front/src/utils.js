export const BASE_URL = "http://192.168.1.102:8095/";
export const GetUsersDataByDate = "admin/user/birthday?date=";
export const GetBlockedUsersUrl = "admin/user/blocked_clients";
export const updateUserBlockUrl = "";
export const sizeArray = [
  { id: 1, value: "SMALL" },
  { id: 2, value: "MEDIUM" },
  { id: 3, value: "LARGE" },
];
export const toppingFillingsArr = [
  { id: 1, value: "MEAT" },
  { id: 2, value: "VEGETABLES" },
  { id: 3, value: "CHEESE" },
  { id: 4, value: "SEAFOOD" },
];
export const stylesArr = [
  { id: 1, value: "CLASSIC_ITALIAN" },
  { id: 2, value: "AMERICAN" },
  { id: 3, value: "SPECIALITY" },
];
export const statusArr = [
  {id:1,value:'NEW'},
  {id:2,value:'PAID'},
  {id:3,value:'CANCELED'}
]
export { default as RegistrationForm } from "./Camponents/Registration_form/RegistrationForm";
export { default as AuthorizationForm } from "./Camponents/AuthorizationForm/AuthorizationForm";
export { default as DoughReqestForm } from "./Camponents/DoughForm/DoughReqestForm";
export { default as IngredientForm } from "./Camponents/IngredienForm/IngredientForm";
export { default as IngredientListPage } from "./Camponents/IngredientListPage/IngredientListPage";
export { default as PizzaForm } from "./Camponents/PizzaForms/PizzaForm";
export { default as GetRevievs } from "./Camponents/GetReviev/GetRevievs";
export { default as PizzaListPage } from "./Pages/PizzaPages/PizzaListPage";

export const saveTokenToLocalstorage = (token) => {
  localStorage.setItem("token", token);
};
export const getTokenFromLocalStorage = () => {
  return localStorage.getItem("token");
};
export const saveRoleToLocalstorage = (role) => {
    localStorage.setItem("role", role);
  };
  export const getRoleFromLocalStorage = () => {
    return localStorage.getItem("role");
  };
  export const saveIdToLocalstorage = (id) => {
    localStorage.setItem("Id", id);
  };
  export const getIdFromLocalStorage = () => {
    return localStorage.getItem("Id");
  };