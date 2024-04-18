import { createAsyncThunk,createSlice } from "@reduxjs/toolkit";
import axios from "axios";
import { BASE_URL } from "../../../utils";

export const getAllComments = createAsyncThunk(
    'getAllComments/getAllComments',
     async()=>{
        try{
            const response = await axios.get(`${BASE_URL}/user/getAllReview`);
            return response.data;
     }catch(error){
        throw error;
     }
    }
)
export const getAllCommentsForPeriod = createAsyncThunk(
    'getAllCommentsForPeriod/getAllCommentsForPeriod',
    async(data)=>{
        const{startDate,endDate}=data;
        try{
            const response = await axios.get(`${BASE_URL}/user/getAllReviewByPeriod?startDate=${startDate}&endDate=${endDate}`);
            return response.data;
     }catch(error){
        throw error;
     }
    }
)
export const getAllCommentsByUserId = createAsyncThunk(
    'getAllCommentsByUserId/getAllCommentsByUserId',
    async(id)=>{
        
        try{
            const response = await axios.get(`${BASE_URL}/user/getAllReviewByUser/${id}`);
            return response.data;
     }catch(error){
        throw error;
     }
    }
)
const userCommentsReqestSlice = createSlice({
    name:'userComments',
    initialState:{
        allRevievsDataState:null,
        allRevievsLoading:false,
        allRevievsError:null,
    },
    reducers:{},
    extraReducers:(builder)=>{
        builder
        .addCase(getAllComments.pending,(state)=>{
            state.allRevievsLoading = true;
            state.allRevievsError = null;
        })
        .addCase(getAllComments.fulfilled,(state,action)=>{
            state.allRevievsDataState = action.payload;
            state.allRevievsLoading = false;
            state.allRevievsError = null;
        })
        .addCase(getAllComments.rejected,(state,action)=>{
            state.allRevievsLoading = false;
            state.allRevievsError = action.payload;
        })
        .addCase( getAllCommentsForPeriod.pending,(state)=>{
            state.allRevievsLoading = true;
            state.allRevievsError = null;
        })
        .addCase( getAllCommentsForPeriod.fulfilled,(state,action)=>{
            state.allRevievsDataState = action.payload;
            state.allRevievsLoading = false;
            state.allRevievsError = null;
        })
        .addCase( getAllCommentsForPeriod.rejected,(state,action)=>{
            state.allRevievsLoading = false;
            state.allRevievsError = action.payload;
        })
        .addCase( getAllCommentsByUserId.pending,(state)=>{
            state.allRevievsLoading = true;
            state.allRevievsError = null;
        })
        .addCase( getAllCommentsByUserId.fulfilled,(state,action)=>{
            state.allRevievsDataState = action.payload;
            state.allRevievsLoading = false;
            state.allRevievsError = null;
        })
        .addCase( getAllCommentsByUserId.rejected,(state,action)=>{
            state.allRevievsLoading = false;
            state.allRevievsError = action.payload;
        })
    }
})

export default userCommentsReqestSlice.reducer;