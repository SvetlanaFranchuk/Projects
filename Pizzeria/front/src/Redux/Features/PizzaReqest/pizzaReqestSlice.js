import { createSlice, createAsyncThunk } from "@reduxjs/toolkit";
import axios from "axios";
import { BASE_URL,getTokenFromLocalStorage } from "../../../utils";

export const addPizza = createAsyncThunk(
  "addPizza/addPizza",
  async ({id, data}) => {
    try {
      const token = getTokenFromLocalStorage();
      const config = {
        headers:{
          Authorization:`Bearer ${token}`,
        }
      }   
      const response = await axios.post(
        `${BASE_URL}pizza/add?userId=${id}`,
        data,config
      );
      return response.data;
    } catch (error) {
      throw error;
    }
  }
);
export const updatePizza = createAsyncThunk(
    'updatePizza/updatePizza',
    async ({id, data}) => {
        try {
          const {
            title,
            description,
            styles,
            toppingsFillings,
            size,
            doughId,
            ingredientsSauceListId,
            ingredientsExtraListId,
            ingredientsBasicListId}= data
            const token = getTokenFromLocalStorage();
            const config = {
              headers:{
                Authorization:`Bearer ${token}`,
              }
            }          
          const response = await axios.put(
            `${BASE_URL}pizza/update/${id}`,
            {title,
            description,
            styles,
            toppingsFillings,
            size,
            doughId,
            ingredientsSauceListId,
            ingredientsExtraListId,
            ingredientsBasicListId},config
          );
          return response.data;
        } catch (error) {
          throw error;
        }
      }
)
export const getStandartPizza = createAsyncThunk(
    'getStandartPizza/getStandartPizza',
    async()=>{
        try{
          const token = getTokenFromLocalStorage();
          const config = {
            headers:{
              Authorization:`Bearer ${token}`,
            }
          }
            const response = await axios.get(`${BASE_URL}pizza/getAllPizzaStandardRecipe`,config);
            return response.data;
        }
        catch(error){
            throw error;
        }
    }
)
export const getPizzaByTopping = createAsyncThunk(
  'getPizzaByTopping/getPizzaByTopping',
  async(data)=>{
    try{
      const token = getTokenFromLocalStorage();
      const config = {
        headers:{
          Authorization:`Bearer ${token}`,
        }
      }
      const response = await axios.get(`${BASE_URL}pizza/getAllPizzaStandardRecipeByTopping?toppingsFillings=${data}`,config)
      return response.data;
    }catch(error){
      throw error;
    }
  }
)
export const getPizzaByStyles = createAsyncThunk(
  'getPizzaByStyles/getPizzaByStyles',
  async(data)=>{
    try{
                const token = getTokenFromLocalStorage();
          const config = {
            headers:{
              Authorization:`Bearer ${token}`,
            }
          }
      const response = await axios.get(`${BASE_URL}pizza/getAllPizzaStandardRecipeByStyles?styles=${data}`,config)
      return response.data;
    }catch(error){
      throw error;
    }
  }
)
export const getPizzaByToppingAndStyles = createAsyncThunk(
  'getPizzaByToppingAndStyles/getPizzaByToppingAndStyles',
  async(data)=>{
    try{
      const{styles,toppingsFillings}= data
      const token = getTokenFromLocalStorage();
      const config = {
        headers:{
          Authorization:`Bearer ${token}`,
        }
      }
      const response = await axios.get(`${BASE_URL}pizza/getAllPizzaStandardRecipeByToppingByStyles?toppingsFillings=${toppingsFillings}&styles=${styles}`,config)
      return response.data;
    }catch(error){
      throw error;
    }
  }
)
export const deletePizza = createAsyncThunk(
  'deletePizza/deletePizza',
  async(id)=>{
    try{
      const token = getTokenFromLocalStorage();
      const config = {
        headers:{
          Authorization:`Bearer ${token}`,
        }
      }
      const response = await axios.delete(`${BASE_URL}pizza/deletePizzaRecipe/${id}`,config)
      return response.data;
    }catch(error){
      throw error;
    }
  }
)
export const getPizzaDetails = createAsyncThunk(
  'getPizzaDetails/getPizzaDetails',
  async(id)=>{
    try{
      const token = getTokenFromLocalStorage();
      const config = {
        headers:{
          Authorization:`Bearer ${token}`,
        }
      }
      const response = await axios.get(`${BASE_URL}pizza/getPizza/${id}`,config)
      return response.data;
    }catch(error){
      throw error;
    }
  }
)

const pizzaReqestSlice = createSlice({
  name: "pizzaReqest",
  initialState: {
    pizzaDetailsState:null,
    pizzaDetailsLoadState:false,
    pizzaDetailsErrorState:null,
    addPizzaDataState: null,
    addpizzaLoadState: false,
    addPizzaErrorState: null,
    allStandartPizzaDataState:null,
    allStandartPizzaLoadState:false,
    allStandartPizzaErrorState:null,
  },
  reducers: {},
  extraReducers: (builder) => {
    builder
      .addCase(addPizza.pending, (state) => {
        state.addpizzaLoadState = true;
        state.addPizzaErrorState = null;
      })
      .addCase(addPizza.fulfilled, (state, action) => {
        state.addPizzaDataState = action.payload;
        state.addpizzaLoadState = false;
        state.addPizzaErrorState = null;
      })
      .addCase(addPizza.rejected, (state, action) => {
        state.addpizzaLoadState = false;
        state.addPizzaErrorState = action.payload;
      })
      .addCase(getStandartPizza.pending, (state) => {
        state.allStandartPizzaLoadState = true;
        state.allStandartPizzaErrorState = null;
      })
      .addCase(getStandartPizza.fulfilled, (state, action) => {
        state.allStandartPizzaDataState = action.payload;
        state.allStandartPizzaLoadState = false;
        state.allStandartPizzaErrorState = null;
      })
      .addCase(getStandartPizza.rejected, (state, action) => {
        state.allStandartPizzaLoadState = false;
        state.allStandartPizzaErrorState = action.payload;
      })
      .addCase(getPizzaByTopping.pending, (state) => {
        state.allStandartPizzaLoadState = true;
        state.allStandartPizzaErrorState = null;
      })
      .addCase(getPizzaByTopping.fulfilled, (state, action) => {
        state.allStandartPizzaDataState = action.payload;
        state.allStandartPizzaLoadState = false;
        state.allStandartPizzaErrorState = null;
      })
      .addCase(getPizzaByTopping.rejected, (state, action) => {
        state.allStandartPizzaLoadState = false;
        state.allStandartPizzaErrorState = action.payload;
      })
      .addCase(getPizzaByStyles.pending, (state) => {
        state.allStandartPizzaLoadState = true;
        state.allStandartPizzaErrorState = null;
      })
      .addCase(getPizzaByStyles.fulfilled, (state, action) => {
        state.allStandartPizzaDataState = action.payload;
        state.allStandartPizzaLoadState = false;
        state.allStandartPizzaErrorState = null;
      })
      .addCase(getPizzaByStyles.rejected, (state, action) => {
        state.allStandartPizzaLoadState = false;
        state.allStandartPizzaErrorState = action.payload;
      })
      .addCase(getPizzaByToppingAndStyles.pending, (state) => {
        state.allStandartPizzaLoadState = true;
        state.allStandartPizzaErrorState = null;
      })
      .addCase(getPizzaByToppingAndStyles.fulfilled, (state, action) => {
        state.allStandartPizzaDataState = action.payload;
        state.allStandartPizzaLoadState = false;
        state.allStandartPizzaErrorState = null;
      })
      .addCase(getPizzaByToppingAndStyles.rejected, (state, action) => {
        state.allStandartPizzaLoadState = false;
        state.allStandartPizzaErrorState = action.payload;
      })
      .addCase(getPizzaDetails.pending, (state) => {
        state.pizzaDetailsLoadState = true;
        state.pizzaDetailsErrorState = null;
      })
      .addCase(getPizzaDetails.fulfilled, (state, action) => {
        state.pizzaDetailsState = action.payload;
        state.pizzaDetailsLoadState = false;
        state.pizzaDetailsErrorState = null;
      })
      .addCase(getPizzaDetails.rejected, (state, action) => {
        state.pizzaDetailsLoadState = false;
        state.pizzaDetailsErrorState = action.payload;
      })
  },
});
export default pizzaReqestSlice.reducer;
