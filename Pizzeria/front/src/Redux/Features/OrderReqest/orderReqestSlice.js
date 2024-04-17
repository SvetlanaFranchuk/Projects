import { createSlice, createAsyncThunk } from "@reduxjs/toolkit";
import axios from "axios";
import { BASE_URL } from "../../../utils";

export const GetOrdersByStatus = createAsyncThunk(
    'GetOrdersByStatus/GetOrdersByStatus',
    async(data)=>{
        try{
            const token = getTokenFromLocalStorage();
            const config = {
              headers:{
                Authorization:`Bearer ${token}`,
              }
            }   
            const response = await axios.get(`${BASE_URL}order/status?status=${data}`,config)
            return response.data
        }catch(error){
            throw error;
        }
    }
)

const orderReqestSlice = createSlice({
    name:'orderReqest',
    initialState:{
        orderDataState:null,
        orderLoadState:false,
        OrderErrorState:null,
    },
    reducers:{},
    extraReducers:(builder)=>{
        builder
        .addCase(GetOrdersByStatus.pending, (state) => {
            state.orderLoadState = true;
            state.OrderErrorState = null;
          })
          .addCase(GetOrdersByStatus.fulfilled, (state, action) => {
            state.orderDataState = action.payload;
            state.orderLoadState = false;
            state.OrderErrorState = null;
          })
          .addCase(GetOrdersByStatus.rejected, (state, action) => {
            state.orderLoadState = false;
            state.OrderErrorState = action.payload;
          })
    }
})