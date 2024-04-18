

function UserItem({userName,email,birthDate,phoneNumber,id,apartmentNumber,city,houseNumber,streetName}){
    return(<>
    <span>User Name</span>
    <p>{userName}</p>
    <span>email</span>
    <p>{email}</p>
    <span>Birthday</span>
    <p>{birthDate}</p>
    <span>Phone Number</span>
    <p>{phoneNumber}</p>
    <span>Id</span>
    <p>{id}</p>
    <ul>
        <p>Adress</p>
        <li>Appartment-{apartmentNumber}</li>
        <li>City-{city}</li>
        <li>House-{houseNumber}</li>
        <li>Street-{streetName}</li>
    </ul>
    </>)

}
export default UserItem;