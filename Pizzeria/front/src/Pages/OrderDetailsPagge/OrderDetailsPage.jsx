import { useSelector, useDispatch } from "react-redux";
import orderStyles from "./orderDetail.module.css";
import { Collapse } from "antd";
import StandartPizzaItem from "../../Camponents/PizzaItems/StandartPizzaItem";
import { updateOrderStatus } from "../../Redux/Features/OrderReqest/orderReqestSlice";
import { statusArr } from "../../utils";


function OrderDetailsPage() {
  const dispatch = useDispatch();
  const orderDetails = useSelector(
    (state) => state.orderSlice.orderDetailState
  );

  const {
    id = null,
    deliveryApartmentNumber = null,
    deliveryCity = null,
    deliveryDateTime = null,
    deliveryHouseNumber = null,
    deliveryStreetName = null,
    orderDateTime = null,
    statusOrder = null,
    sum = null,
    typeBonus = null,
    userAppId = null,
    pizzaIdToCount = [],
  } = orderDetails && orderDetails.orderData ? orderDetails.orderData : {};

  const orderPizzaListLabel =
    orderDetails &&
    orderDetails.results.map((item) => ({
      label: (
        <div>
          <p>{item.title}</p>
          <p>count : {item.count}</p>
        </div>
      ),
      key: item.id,
      children: (
        <StandartPizzaItem
          amount={item.amount}
          ingredientsList={item.ingredientsList}
          nutrition={item.nutrition}
          size={item.size}
          styles={item.styles}
          title={item.title}
          toppingsFillings={item.toppingsFillings}
        ></StandartPizzaItem>
      ),
    }));
    const handleUpdateOrderStatus = (id,event)=>{
      const value = event.target.value;
      console.log(value)
      dispatch(updateOrderStatus({id:id,value:value}))
    }

  return (
    <div className={orderStyles.orderDetailWrapper}>
      {orderDetails && (
        <div className={orderStyles.orderContainer}>
          <h2 style={{ color: "black" }}>OrderDetail</h2>
          <div>
            <ul>
              <h3>UserInfo</h3>
              <li>Order Id : {id}</li>
              <li>User ID : {userAppId}</li>
              <h3>Adress</h3>
              <li>City : {deliveryCity}</li>
              <li>Street : {deliveryStreetName}</li>
              <li>House : {deliveryHouseNumber}</li>
              {deliveryApartmentNumber && (
                <li>Appartment : {deliveryApartmentNumber}</li>
              )}
              <h3>Order Info</h3>
              {pizzaIdToCount &&
                pizzaIdToCount.map((item) => (
                  <div
                    className={orderStyles.pizzaListContainer}
                    key={item.pizzaId}
                  >
                    <ul>
                    <Collapse accordion style={{background:'white'}} items={orderPizzaListLabel}/>
                    </ul>
                  </div>
                ))}
              <li>Order Status : {statusOrder}</li>
              <li>Ordered : {orderDateTime}</li>
              <li>Delivered : {deliveryDateTime}</li>
              {typeBonus && <li>Bonus : {typeBonus}</li>}
              <li>Sum : {sum}</li>
            </ul>
          </div>
          <select name="statusSelect" id="" onChange={(event) => handleUpdateOrderStatus(id, event)}>
                {statusArr&&statusArr.map((item)=>(
                    <option key={item.id} value={item.value}>{item.value}</option>
                ))}
        </select>
        </div>
      )}
    </div>
  );
}

export default OrderDetailsPage;
