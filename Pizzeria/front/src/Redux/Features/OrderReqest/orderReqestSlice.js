import { createSlice, createAsyncThunk } from "@reduxjs/toolkit";
import axios from "axios";
import { BASE_URL,getTokenFromLocalStorage} from "../../../utils";


export const getOrdersByStatus = createAsyncThunk(
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
export const getOrdersForPeriod = createAsyncThunk(
  'getOrdersForPeriod/getOrdersForPeriod',
  async(data)=>{
    try{
      const token = getTokenFromLocalStorage();
      const config = {
        headers:{
          Authorization:`Bearer ${token}`,
        }
      }
      const response = await axios.get(`${BASE_URL}order/period?startDate=${data.startDate}&endDate=${data.endDate}`,config) 
      return response.data;  
    }catch(error){
      throw error;
    }
  }
)
export const getOrdersDetails = createAsyncThunk(
  'getOrdersDetails/getOrdersDetails',
  async (id, thunkAPI) => {
    try {
      const token = getTokenFromLocalStorage();
      const config = {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      };
      const orderResponse = await axios.get(`${BASE_URL}order/${id}`, config);
      const orderData = orderResponse.data;
      const promises = orderData.pizzaIdToCount.map(async (item) => {
        const response = await axios.get(`${BASE_URL}pizza/getPizza/${item.pizzaId}`, config);
        const resultData = response.data;
        if(item.pizzaId===resultData.id){
          resultData.count = item.count
        }
        return resultData;
      });
      const results = await Promise.all(promises);
      return { orderData, results };
    } catch (error) {
      throw error;
    }
  }
);
export const updateOrderStatus = createAsyncThunk(
  'updateOrderStatus/updateOrderStatus',
  async({id,value})=>{
    try{
      const token = getTokenFromLocalStorage();
      const config = {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      };
      const response = await axios.patch(`${BASE_URL}order/${id}/status?status=${value}`,null,config)
      return response.data;
    }catch(error){
      throw error;
    }
  }
);

const orderReqestSlice = createSlice({
    name:'orderReqest',
    initialState:{
        orderDataState:null,
        orderLoadState:false,
        OrderErrorState:null,
        orderDetailState:null,
        orderDetailLoadingState:false,
        orderDetailErrorState:null,
    },
    reducers:{},
    extraReducers:(builder)=>{
        builder
        .addCase(getOrdersByStatus.pending, (state) => {
            state.orderLoadState = true;
            state.OrderErrorState = null;
          })
          .addCase(getOrdersByStatus.fulfilled, (state, action) => {
            state.orderDataState = action.payload;
            state.orderLoadState = false;
            state.OrderErrorState = null;
          })
          .addCase(getOrdersByStatus.rejected, (state, action) => {
            state.orderLoadState = false;
            state.OrderErrorState = action.payload;
          })
          .addCase(getOrdersForPeriod.pending, (state) => {
            state.orderLoadState = true;
            state.OrderErrorState = null;
          })
          .addCase(getOrdersForPeriod.fulfilled, (state, action) => {
            state.orderDataState = action.payload;
            state.orderLoadState = false;
            state.OrderErrorState = null;
          })
          .addCase(getOrdersForPeriod.rejected, (state, action) => {
            state.orderLoadState = false;
            state.OrderErrorState = action.payload;
          })
          .addCase(getOrdersDetails.pending, (state) => {
            state.orderDetailLoadingState = true;
            state.orderDetailErrorState = null;
          })
          .addCase(getOrdersDetails.fulfilled, (state, action) => {
            state.orderDetailState = action.payload;
            state.orderDetailLoadingState = false;
            state.orderDetailErrorState = null;
          })
          .addCase(getOrdersDetails.rejected, (state, action) => {
            state.orderDetailLoadingState = false;
            state.orderDetailErrorState = action.payload;
          })
    }
})

export default orderReqestSlice.reducer;