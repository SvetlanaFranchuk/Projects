import {Collapse} from 'antd'
import { useDispatch, useSelector } from 'react-redux';
import { getOrdersDetails } from '../../Redux/Features/OrderReqest/orderReqestSlice';
import { useNavigate } from 'react-router-dom';



function RenderOrdersList(){
    const dispatch = useDispatch();
    const navigate = useNavigate();
const ordersList = useSelector((state)=>state.orderSlice.orderDataState);
const handleOrderDetails = (id)=>{
    dispatch(getOrdersDetails(id))
    navigate('/order-details')
}
const orderListLabels = ordersList&&ordersList.map((item)=>({
    label: <span style={{ color: "black" }}>order Id:{item.id}</span>,
    key: item.id,
    children:(<div>
        <ul>
            <li>sum:{item.sum}</li>
            <li>statusOrder:{item.statusOrder}</li>
            <li>orderDateTime:{item.orderDateTime}</li>
            <li>userAppId:{item.userAppId}</li>
        </ul>
        <button  onClick={()=>handleOrderDetails(item.id)}>Get order details</button>
        </div>
    )
}))



    return(<>
    <Collapse items={orderListLabels} accordion style={{background:'white'}}/>
    </>)
}

export default RenderOrdersList;