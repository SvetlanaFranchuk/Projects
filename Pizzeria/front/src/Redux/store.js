import { configureStore } from "@reduxjs/toolkit";
import userRegReducer from './Features/userReqests/userRegistrationSlice.js';
import userAuthorizationSlice from "./Features/userReqests/userAuthorizationSlice.js";
import doughReducer from '../Redux/Features/AdminReqests/doughReqest.js'
import ingredientReducer from '../Redux/Features/AdminReqests/ingredientReqestsSlice.js'
import userCommentsReqestSlice from "./Features/userReqests/userCommentsReqestSlice.js";
import pizzaReqestSlice from "./Features/PizzaReqest/pizzaReqestSlice.js";
import  getUserDataByBirthDay  from "./Features/userReqests/getUserData.js";
import orderReqestSlice from "./Features/OrderReqest/orderReqestSlice.js";

export const store = configureStore({
    reducer:{
        userReg:userRegReducer,
        userLogin:userAuthorizationSlice,
        doughReqest:doughReducer,
        ingredientReqest:ingredientReducer,
        userComments:userCommentsReqestSlice,
        pizzaReqest:pizzaReqestSlice,
        getUsersDataByBirthday:getUserDataByBirthDay,
        orderSlice:orderReqestSlice,
    }
})