import { createSlice } from "@reduxjs/toolkit";
import { createAsyncThunk } from "@reduxjs/toolkit";
import axios from "axios";
import { BASE_URL } from "../../../utils";

export const userRegReqest = createAsyncThunk(
    'userReg/userregReqest',
    async(data)=>{
        try{
            const response= await axios.post(`${BASE_URL}auth/register`,data)
            return response.data;
        }catch(error){
            throw error;
        }
    }
)

const userRegSlice = createSlice({
    name:'userReg',
    initialState:{
        userregReqest: null,
        loading:false,
        error:null,
    },
    reducers:{},
    extraReducers:(builder) => {
        builder
          .addCase(userRegReqest.pending, (state) => {
            state.loading = true;
            state.error = null;
          })
          .addCase(userRegReqest.fulfilled, (state, action) => {
            state.loading = false;
            state.userregReqest = action.payload;
          })
          .addCase(userRegReqest.rejected, (state, action) => {
            state.loading = false;
            state.error = action.error.message;
          });
      }
})
 export default userRegSlice.reducer;