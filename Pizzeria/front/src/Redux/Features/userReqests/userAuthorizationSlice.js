import { createSlice } from "@reduxjs/toolkit";
import { createAsyncThunk } from "@reduxjs/toolkit";
import axios from "axios";
import { BASE_URL} from "../../../utils";

export const LoginReqest = createAsyncThunk(
  'userLogin/LoginReqest',
  async (data)=>{
    try{
      const response = await axios.post(`${BASE_URL}auth/authentication`,data)
      return response.data;
    }catch(error){
      throw error;
    }
  }
)

const userAuthorizationSlice = createSlice({
    name: 'userAuthorization',
    initialState:{
        userDataState:null,
        loadingState:false,
        errorState:null,
        userRoleState:null,
    },
    reducers:{

    },
    extraReducers:(builder)=>{
        builder
        .addCase(LoginReqest.pending, (state) => {
          state.loadingState = true;
          state.errorState = null;
        })
        .addCase(LoginReqest.fulfilled, (state, action) => {
          state.loadingState = false;
          state.userDataState = action.payload;
        })
        .addCase(LoginReqest.rejected, (state, action) => {
          state.loadingState = false;
          state.errorState = action.error.message;
        });

    }
})
export const { handleUserRoleChange } = userAuthorizationSlice.actions;
export default userAuthorizationSlice.reducer;