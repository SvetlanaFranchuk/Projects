import { useDispatch, useSelector } from "react-redux";
import {getAllCommentsForPeriod,getAllCommentsByUserId} from "../../Redux/Features/userReqests/userCommentsReqestSlice";
import { useForm, Controller } from "react-hook-form";

function GetRevievs() {
  const dispatch = useDispatch();
  const { control, handleSubmit } = useForm();
  const allRevievs = useSelector(
    (state) => state.userComments.allRevievsDataState
  );
  const userid = '1'
  const handleGetAllRevievs = () => {
    dispatch(getAllCommentsByUserId(userid));
  };
  const handleGetRevievsForPeriod = (data)=>{
    dispatch(getAllCommentsForPeriod(data))
  }
  console.log(allRevievs)
  return (
    <>
      <button onClick={handleGetAllRevievs}>All Revievs</button>
      <form onSubmit={handleSubmit(handleGetRevievsForPeriod)}>
        <label htmlFor="">Starts from</label>
        <Controller
          name="startDate"
          control={control}
          defaultValue=""
          render={({ field }) => (
            <input
              type="datetime-local"
              {...field}
              id="startDate"
              min="2010-01-01T00:00"
              max="2050-01-01T23:59"
              step="1"
            />
          )}
        />
        <label htmlFor="">Finished on</label>
        <Controller
          name="endDate"
          control={control}
          defaultValue=""
          render={({ field }) => (
            <input
              type="datetime-local"
              {...field}
              id="endDate"
              min="2010-01-01T00:00"
              max="2050-01-01T23:59"
              step="1"
            />
          )}
        />

        <input type="submit" />
      </form>
    </>
  );
}
export default GetRevievs;
