import { useDispatch } from "react-redux";
import { statusArr } from "../../utils";
import { getOrdersByStatus } from "../../Redux/Features/OrderReqest/orderReqestSlice";

function GetOrdersByStatus(){
    const dispatch = useDispatch()
    const handleStatusselect = (event)=>{
        const value = event.target.value;
        dispatch(getOrdersByStatus(value))
    }

    return(<div>
        <select name="statusSelect" id="" onChange={handleStatusselect}>
                {statusArr&&statusArr.map((item)=>(
                    <option key={item.id} value={item.value}>{item.value}</option>
                ))}
        </select>
    </div>)
}
export default GetOrdersByStatus;