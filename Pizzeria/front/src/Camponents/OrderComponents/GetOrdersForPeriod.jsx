import { useDispatch } from "react-redux";
import { getOrdersForPeriod } from "../../Redux/Features/OrderReqest/orderReqestSlice";
import { useForm,Controller } from "react-hook-form";


function GetOrdersForPeriod() {
    const dispatch = useDispatch();
    const{
        handleSubmit,
        control,
    }=useForm();
    const currentDate = new Date();
    const year = currentDate.getFullYear();
    const month = String(currentDate.getMonth()+1).padStart(2,'0');
    const day = String(currentDate.getDate()-1).padStart(2,'0');
    const dateString = `${year}-${month}-${day}`;
  

    const handleGetOrdersForPeriod = (data)=>{
        dispatch(getOrdersForPeriod(data));
    }

  return (
    <div>
      <form onSubmit={handleSubmit(handleGetOrdersForPeriod)}>
        <label htmlFor="">Starts from</label>
        <Controller
          name="startDate"
          control={control}
          rules={{required:true}}
          defaultValue=""
          render={({ field }) => (
            <input
              type="date"
              {...field}
              id="startDate"
              min="2010-01-01"
              max="2050-01-01"
              step="1"
            />
          )}
        />
        <label htmlFor="">Finished on</label>
        <Controller
          name="endDate"
          control={control}
          defaultValue={dateString}
          render={({ field }) => (
            <input
              type="date"
              {...field}
              id="endDate"
              min="2010-01-01"
              max="2050-01-01"
              step="1"
            />
          )}
        />

        <input type="submit" />
      </form>
    </div>
  );
}
export default GetOrdersForPeriod;