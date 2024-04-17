import {useDispatch} from 'react-redux'
import { updateUserBlocking } from '../../Redux/Features/userReqests/getUserData';

function BlockedUserItem({ id, userName, reviewDate, blocked }) {
    const dispatch = useDispatch();
    const handleChange = (event)=>{
        const value = event.target.value;
        
        dispatch(updateUserBlocking({id,value:value==='true'}));
    }

  return (
    <div>
      <h3>{userName}</h3>
      <ul>
        <li>user ID :{id}</li>
        <li>Review Date:{reviewDate} </li>
        <li>
          <select value={blocked.toString()} onChange={handleChange}>
            <option value='true'>Blocked</option>
            <option value='false'>Active</option>
          </select>
        </li>
      </ul>
    </div>
  );
}
export default BlockedUserItem;
