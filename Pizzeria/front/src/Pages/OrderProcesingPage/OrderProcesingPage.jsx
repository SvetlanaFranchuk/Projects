import GetOrdersByStatus from "../../Camponents/OrderComponents/GetOrderByStatus";
import GetOrdersForPeriod from "../../Camponents/OrderComponents/GetOrdersForPeriod";
import RenderOrdersList from "../../Camponents/OrderComponents/RenderOrdersList";
import { useSelector } from 'react-redux';


function OrderProcesingPage(){

    const ordersList = useSelector((state)=>state.orderSlice.orderDataState);

    return(
        <>
        <GetOrdersByStatus></GetOrdersByStatus>
        <GetOrdersForPeriod></GetOrdersForPeriod>
        {ordersList&&<RenderOrdersList></RenderOrdersList>}
        </>
    )
}
export default OrderProcesingPage;