import {createSlice} from '@reduxjs/toolkit';
import { createAsyncThunk } from '@reduxjs/toolkit';
import axios from 'axios';
import { BASE_URL,getTokenFromLocalStorage} from '../../../utils';

export const doughReqest = createAsyncThunk(
    'doughReq/doughReqest',
    async (data)=>{
        try{
            const token = getTokenFromLocalStorage();
            const config = {
              headers:{
                Authorization:`Bearer ${token}`,
              }
            }   
            const response = await axios.post(`${BASE_URL}dough/add`,data,config)
            return response.data;
            
        }catch(error){
            throw error;
        }
    }
);
export const getAllDough = createAsyncThunk(
    'getAllDough/getAllDough',
    async ()=>{
        try{
            const token = getTokenFromLocalStorage();
            const config = {
              headers:{
                Authorization:`Bearer ${token}`,
              }
            }   
            const response = await axios.get(`${BASE_URL}dough/getAllForAdmin`,config)
      
            return response.data;
            
        }catch(error){
            throw error;
        }
    }
);
export const updateDough = createAsyncThunk(
    'updateDough/updateDough',
    async ({id,data}) => {
       
        const {smallNutrition,smallWeight}=data
        try {
            const token = getTokenFromLocalStorage();
            const config = {
              headers:{
                Authorization:`Bearer ${token}`,
              }
            }   
            const response = await axios.patch(`${BASE_URL}dough/update/${id}`,{...data,
            smallWeight: parseInt(smallWeight),
            smallNutrition: parseInt(smallNutrition)
        },config);
            return response.data;
        } catch (error) {
            throw error;
        }
    }
);
export const doughDelete = createAsyncThunk(
    'doughDelete/doughDelete',
    async (id)=>{
        try{
            const token = getTokenFromLocalStorage();
            const config = {
              headers:{
                Authorization:`Bearer ${token}`,
              }
            }        
            const response = await axios.delete(`${BASE_URL}dough/delete/${id}`,config)
            return response.data;
        }catch(error){
            throw error;
        }
    }
);
const doughReqestSlice = createSlice({
    name:'doughReqest',
    initialState:{
        doughDataState:null,
        loadingState:false,
        errorState:null,
        allDoughDataState:null,
        allDoughLoadingState:false,
        allDoughErrorState:null,
    },
    reducers:{},
    extraReducers:(builder)=>{
        builder
        .addCase(doughReqest.pending,(state)=>{
            state.loadingState = true;
            state.errorState = null;
        })
        .addCase(doughReqest.fulfilled,(state,action)=>{
            state.doughDataState = action.payload;
            state.loadingState = false;
            state.errorState = null;
        })
        .addCase(doughReqest.rejected,(state,action)=>{
            state.loadingState = false;
            state.errorState = action.payload;
        })
        .addCase(getAllDough.pending,(state)=>{
            state.allDoughLoadingState = true;
            state.allDoughErrorState = null;
        })
        .addCase(getAllDough.fulfilled,(state,action)=>{
            state.allDoughDataState = action.payload;
            state.allDoughLoadingState = false;
            state.allDoughErrorState = null;
        })
        .addCase(getAllDough.rejected,(state,action)=>{
            state.allDoughLoadingState = false;
            state.allDoughErrorState = action.payload;
        })
        .addCase(updateDough.pending, (state) => {
            state.loadingState = true;
            state.errorState = null;
          })
          .addCase(updateDough.fulfilled, (state, action) => {
            state.doughDataState = action.payload;
            state.loadingState = false;
            state.errorState = null;
          })
          .addCase(updateDough.rejected, (state, action) => {
            state.loadingState = false;
            state.errorState = action.payload;
          });
    }

})
export default doughReqestSlice.reducer;