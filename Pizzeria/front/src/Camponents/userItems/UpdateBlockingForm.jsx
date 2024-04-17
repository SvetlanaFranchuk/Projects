import { useForm, Controller } from "react-hook-form";
import { useDispatch} from "react-redux";
import { updateUserBlocking } from "../../Redux/Features/userReqests/getUserData";

function UpdateBlockingForm(){
    const { register, handleSubmit, control } = useForm();
    const dispatch = useDispatch();
    const submitBlocking = (data) => {
        const {userId,userStatus} = data
        console.log(userId,userStatus)
        dispatch(updateUserBlocking({id:userId,value:userStatus}));
      };

    return(
        <form onSubmit={handleSubmit(submitBlocking)}>
        <label style={{color:'#17D861'}} htmlFor="userId">User ID</label>
        <input type="number" {...register("userId",{required:true})} />
        <label htmlFor="userStatus" style={{color:'#17D861'}}>User Status</label>
        <Controller
          name="userStatus"
          control={control}
          rules={{ required: true }}
          defaultValue=""
          render={({ field }) => (
            <select {...field}>
              <option value="">Select status</option>
              <option value="true">Blocked</option>
              <option value="false">Active</option>
            </select>
          )}
        />
        <input type="submit" />
      </form>
    )
}
export default UpdateBlockingForm;