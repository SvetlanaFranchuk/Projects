import {createSlice} from '@reduxjs/toolkit';
import { createAsyncThunk } from '@reduxjs/toolkit';
import axios from 'axios';
import { BASE_URL,getTokenFromLocalStorage } from '../../../utils';

export const addIngredient = createAsyncThunk(
    'addIngredient/addIngredient',
    async (data)=>{
        try{
            const token = getTokenFromLocalStorage();
            const config = {
              headers:{
                Authorization:`Bearer ${token}`,
              }
            }   
            const response = await axios.post(`${BASE_URL}product/addIngredient`,data,config)
            return response.data;
            
        }catch(error){
            throw error;
        }
    }
)
export const getIngredients = createAsyncThunk(
    'getIngredient/getIngredient',
    async (data)=>{
        try{
            const token = getTokenFromLocalStorage();
            const config = {
              headers:{
                Authorization:`Bearer ${token}`,
              }
            }   
            const response = await axios.get(`${BASE_URL}product/getAllIngredientByGroup?groupIngredient=${data}`,config)
            return response.data;
            
        }catch(error){
            throw error;
        }
    }
)
export const getSauce = createAsyncThunk(
    'getSauce/getSauce',
    async ()=>{
        try{
            const token = getTokenFromLocalStorage();
            const config = {
              headers:{
                Authorization:`Bearer ${token}`,
              }
            }   
            const response = await axios.get(`${BASE_URL}product/getAllIngredientByGroup?groupIngredient=SAUCE`,config)
            
            return response.data;
            
        }catch(error){
            throw error;
        }
    }
)
export const getExtra = createAsyncThunk(
    'getExtra/getExtra',
    async ()=>{
        try{
            const token = getTokenFromLocalStorage();
            const config = {
              headers:{
                Authorization:`Bearer ${token}`,
              }
            }   
            const response = await axios.get(`${BASE_URL}product/getAllIngredientByGroup?groupIngredient=EXTRA`,config)
           
            return response.data;
            
        }catch(error){
            throw error;
        }
    }
)
export const getBasic = createAsyncThunk(
    'getBasic/getBasic',
    async ()=>{
        try{
            const token = getTokenFromLocalStorage();
            const config = {
              headers:{
                Authorization:`Bearer ${token}`,
              }
            }   
            const response = await axios.get(`${BASE_URL}product/getAllIngredientByGroup?groupIngredient=BASIC`,config)
           
            return response.data;
            
        }catch(error){
            throw error;
        }
    }
)
export const updateIngredient = createAsyncThunk(
    'updateIngredient/updateIngredient',
    async ({ id, data }) => {
        const{name, weight, nutrition, price, groupIngredient} = data;
      try {
        const token = getTokenFromLocalStorage();
        const config = {
          headers:{
            Authorization:`Bearer ${token}`,
          }
        }   
        const response = await axios.put(`${BASE_URL}product/updateIngredient/${id}`, {
          name,
          weight,
          nutrition,
          price,
          groupIngredient
        },config);
        return response.data;
      } catch (error) {
        throw error;
      }
    }
  );
  
export const ingredientDelete = createAsyncThunk(
    'ingredientDelete/ingredientDelete',
    async (id)=>{
        try{
            const token = getTokenFromLocalStorage();
            const config = {
              headers:{
                Authorization:`Bearer ${token}`,
              }
            }   
            const response = await axios.delete(`${BASE_URL}product/deleteIngredient/${id}`,config)
            return response.data;
        }catch(error){
            throw error;
        }
    }
)

const   ingredientreqestSlice = createSlice({
    name:'ingredientReqest',
    initialState:{
        ingredientDataState:null,
        ingredientLoadState:false,
        ingredientErrorState:null,
        ingredientListDataState:null,
        ingredientListLoadState:false,
        ingredientListErrorState:null,
        sauceDataState:null,
        sauceLoadState:false,
        sauceErrorState:null,
        extraDataState:null,
        extraLoadState:false,
        extraErrorState:null,
        basicDataState:null,
        basicLoadState:false,
        basicErrorState:null,
        groupIngredientState:null,
        isTrue:false,
    },
    reducers:{
        SetGroupIngredient:(state,action)=>{
            state.groupIngredientState = action.payload;
        },
        SetIsTrue:(state,action)=>{
            state.isTrue = action.payload;
        }
    },
    extraReducers:(builder)=>{
        builder
        .addCase(addIngredient.pending,(state)=>{
            state.ingredientLoadState = true;
            state.ingredientErrorState = null;
        })
        .addCase(addIngredient.fulfilled,(state,action)=>{
            state.ingredientDataState = action.payload;
            state.ingredientLoadState = false;
            state.ingredientErrorState = null;
        })
        .addCase(addIngredient.rejected,(state,action)=>{
            state.ingredientLoadState = false;
            state.ingredientErrorState = action.payload;
        })
        .addCase(getIngredients.pending,(state)=>{
            state.ingredientListLoadState = true;
            state.ingredientListErrorState = null;
        })
        .addCase(getIngredients.fulfilled,(state,action)=>{
            state.ingredientListDataState = action.payload;
            state.ingredientListLoadState = false;
            state.ingredientListErrorState = null;
        })
        .addCase(getIngredients.rejected,(state,action)=>{
            state.ingredientListLoadState = false;
            state.ingredientListErrorState = action.payload;
        })
        .addCase(getSauce.pending,(state)=>{
            state.sauceLoadState = true;
            state.sauceErrorState = null;
        })
        .addCase(getSauce.fulfilled,(state,action)=>{
            state.sauceDataState = action.payload;
            state.sauceLoadState = false;
            state.sauceErrorState = null;
        })
        .addCase(getSauce.rejected,(state,action)=>{
            state.sauceLoadState = false;
            state.sauceErrorState = action.payload;
        })
        .addCase(getBasic.pending,(state)=>{
            state.basicLoadState = true;
            state.basicErrorState = null;
        })
        .addCase(getBasic.fulfilled,(state,action)=>{
            state.basicDataState = action.payload;
            state.basicLoadState = false;
            state.basicErrorState = null;
        })
        .addCase(getBasic.rejected,(state,action)=>{
            state.basicLoadState = false;
            state.basicErrorState = action.payload;
        })
        .addCase(getExtra.pending,(state)=>{
            state.extraLoadState = true;
            state.extraErrorState = null;
        })
        .addCase(getExtra.fulfilled,(state,action)=>{
            state.extraDataState = action.payload;
            state.extraLoadState = false;
            state.extraErrorState = null;
        })
        .addCase(getExtra.rejected,(state,action)=>{
            state.extraLoadState = false;
            state.extraErrorState = action.payload;
        })
    }
})
export  const {SetGroupIngredient,SetIsTrue} =ingredientreqestSlice.actions;
export default ingredientreqestSlice.reducer;