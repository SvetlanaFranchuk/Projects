import { Select } from "antd";
import { useNavigate } from "react-router-dom";
import navStyles from './adminNavBar.module.css'



function AdminNavBar(){
    const {Option,OptGroup} = Select;
    const navigate = useNavigate();

  
    const handleChange = (value)=>{
        
        navigate(`${value}`);
    };

    return(
        <div className={navStyles.navContainer}>
            <Select className={navStyles.navSelect} defaultValue='Navigate'onChange={handleChange} >
               <OptGroup label = 'Users'/>
                <Option value='userBlocking' onSelect={handleChange}>Blocking</Option>
                <Option value = 'admin-orders' onSelect={handleChange}></Option>
                <OptGroup label = 'Users-statistics'/>
                <Option value='usersData' onSelect={handleChange}>Get users data</Option>
                <OptGroup label = 'manegment-statistics'/>
                <Option value = 'standart-pizza-list' onSelect={handleChange}></Option>
                <Option value = 'add-pizza' onSelect={handleChange}></Option>
                <Option value = 'ingredients-list' onSelect={handleChange}></Option>          
            </Select>   

        </div>
    )
}
export default AdminNavBar;