import { createAsyncThunk,createSlice } from "@reduxjs/toolkit";
import axios from "axios";
import { BASE_URL, GetUsersDataByDate,GetBlockedUsersUrl } from "../../../utils";
import { getTokenFromLocalStorage } from "../../../utils";

export const getAllClientsData = createAsyncThunk(
    'getAllClientsData/getAllClientsData',
    async()=>{
        try{
            const token = getTokenFromLocalStorage();
            const config = {
              headers:{
                Authorization:`Bearer ${token}`,
              }
            }  
            const response = await axios.get(`${BASE_URL}admin/user/clients`,config);
            return response.data;
        }catch(error){
            throw error;
        }
    }
)

export const getUserDataByBirthDay = createAsyncThunk(
    'getUserDataByBirthDay/getUserDataByBirthDay',
async(data)=>{
    try{
        const token = getTokenFromLocalStorage();
        const config = {
          headers:{
            Authorization:`Bearer ${token}`,
          }
        }  
        const response = await axios.get(`${BASE_URL}${GetUsersDataByDate}${data}`,config)
        return response.data;
    }catch(error){
        throw error;
    }
}
);
export const getBlockedUserData = createAsyncThunk(
    'getBlockedUserData/getBlockedUserData',
    async()=>{
        try{
            const token = getTokenFromLocalStorage();
            const config = {
              headers:{
                Authorization:`Bearer ${token}`,
              }
            }  
            const response = await axios.get(`${BASE_URL}${GetBlockedUsersUrl}`,config)
            return response.data;
        }catch(error){
            throw error;
        }
    }
);
export const updateUserBlocking = createAsyncThunk(
    'updateUserBlocking/updateUserBlocking',
    async({id,value})=>{
        try{
            const token = getTokenFromLocalStorage();
            const config = {
              headers:{
                Authorization:`Bearer ${token}`,
              }
            }  
            
            const response = await axios.put(`${BASE_URL}admin/user/change_blocking/${id}?isBlocked=${value}`,null,config)
            return response.data;
        }catch(error){
            throw error;
        }
    }
) 
const getUsersDataByBirthday = createSlice({
    name:'getUsersDataByBirthday',
    initialState:{
        userDataState:null,
        userDataLoadState:false,
        userDataErrorState:null,
        blockedUserDataState:null,
    },
    reducers:{},
    extraReducers:(builder)=>[
        builder
        .addCase(getUserDataByBirthDay.pending,(state)=>{
            state.userDataLoadState = true;
            state.userDataErrorState = null;
        })
        .addCase(getUserDataByBirthDay.fulfilled,(state,action)=>{
            state.userDataState = action.payload;
            state.userDataLoadState = false;
            state.userDataErrorState = null;
        })
        .addCase(getUserDataByBirthDay.rejected,(state,action)=>{
            state.userDataLoadState = false;
            state.userDataErrorState = action.payload;
        })
        .addCase(getBlockedUserData.pending,(state)=>{
            state.userDataLoadState = true;
            state.userDataErrorState = null;
        })
        .addCase(getBlockedUserData.fulfilled,(state,action)=>{
            state.blockedUserDataState = action.payload;
            state.userDataLoadState = false;
            state.userDataErrorState = null;
        })
        .addCase(getBlockedUserData.rejected,(state,action)=>{
            state.userDataLoadState = false;
            state.userDataErrorState = action.payload;
        })
        .addCase(getAllClientsData.pending,(state)=>{
            state.userDataLoadState = true;
            state.userDataErrorState = null;
        })
        .addCase(getAllClientsData.fulfilled,(state,action)=>{
            state.userDataState = action.payload;
            state.userDataLoadState = false;
            state.userDataErrorState = null;
        })
        .addCase(getAllClientsData.rejected,(state,action)=>{
            state.userDataLoadState = false;
            state.userDataErrorState = action.payload;
        })
    ]
})
export default getUsersDataByBirthday.reducer;


