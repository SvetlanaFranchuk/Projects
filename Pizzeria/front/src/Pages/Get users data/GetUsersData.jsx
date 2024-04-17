import { useEffect } from "react";
import { Collapse } from "antd";
import { useForm } from "react-hook-form";
import { useDispatch, useSelector } from "react-redux";
import {
  getUserDataByBirthDay,
  getBlockedUserData,
  getAllClientsData,
} from "../../Redux/Features/userReqests/getUserData";
import UserItem from "../../Camponents/userItems/UserItem";
import BlockedUserItem from "../../Camponents/userItems/BlockedUserItem";
import UpdateBlockingForm from "../../Camponents/userItems/UpdateBlockingForm";

function GetUsersData() {
  const dispatch = useDispatch();
  const userData = useSelector(
    (state) => state.getUsersDataByBirthday.userDataState
  );
  const blockedUsers = useSelector(
    (state) => state.getUsersDataByBirthday.blockedUserDataState
  );

  const { register, handleSubmit, control } = useForm();

  const submit = (date) => {
    dispatch(getUserDataByBirthDay(date.birthday));
  };
  const getBlocked = () => {
    dispatch(getBlockedUserData());
  };
 const getAllClients = ()=>{
    dispatch(getAllClientsData())
 }

  const userLabels =
    userData &&
    userData.map((item) => ({
      label: <span style={{ color: "white" }}>{item.userName}</span>,
      key: item.id,
      children: (
        <UserItem
          userName={item.userName}
          email={item.email}
          birthDate={item.birthDate}
          phoneNumber={item.phoneNumber.phoneNumber}
          id={item.id}
          apartmentNumber={item.address.apartmentNumber}
          city={item.address.city}
          houseNumber={item.address.houseNumber}
          streetName={item.address.streetName}
        ></UserItem>
      ),
    }));
  const blockedUserLabels =
    blockedUsers &&
    blockedUsers.map((item) => ({
      label: <span style={{ color: "white" }}>{item.userName}</span>,
      key: item.id,
      children: (
        <BlockedUserItem
          userName={item.userName}
          id={item.id}
          reviewDate={item.reviewDate}
          blocked={item.blocked}
        ></BlockedUserItem>
      ),
    }));
  return (
    <div>
      <form onSubmit={handleSubmit(submit)}>
        <label htmlFor="birthday">Birth Date</label>
        <input type="date" {...register("birthday",{required:true})} />
        <input type="submit" value="get info" />
      </form>
      <UpdateBlockingForm></UpdateBlockingForm>
      <button onClick={getBlocked}>Get Blocked Users</button>
      <button onClick={getAllClients}>Get All Clients</button>
      {userLabels && <Collapse accordion items={userLabels} />}
      {blockedUserLabels && <Collapse accordion items={blockedUserLabels} />}
    </div>
  );
}
export default GetUsersData;
