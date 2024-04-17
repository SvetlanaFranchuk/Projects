import { useSelector} from "react-redux";
import AdminNavBar from "./AdminNavBar";
import {useNavigate} from 'react-router-dom'
import { useEffect, useState } from "react";
import { getRoleFromLocalStorage,saveRoleToLocalstorage,saveTokenToLocalstorage,saveIdToLocalstorage } from "../../utils";
import { globalConfig } from "antd/es/config-provider";


function NavBar(){
    const userData = useSelector((state)=>state.userLogin.userDataState);
    const [role, setRole] = useState(getRoleFromLocalStorage());
    useEffect(() => {
      if (userData && userData.token&&userData.role) {
        saveTokenToLocalstorage(userData.token);
        saveRoleToLocalstorage(userData.role);
        saveIdToLocalstorage(userData.userResponseDto.id)
        setRole(userData.role);
      }
    }, [userData]);
    
    const navigate = useNavigate()
    const handleNavToMain = ()=>{
        navigate('/')
    }
    const handleRegister = ()=>{
        navigate('register')
    }
    return(
        <div>
            <button onClick={handleNavToMain}>Main Page</button>
            <button onClick={handleRegister}>Register</button>
            {role&&role==='ROLE_ADMIN'&&<AdminNavBar></AdminNavBar>}
        </div>
    )

}
export default NavBar;