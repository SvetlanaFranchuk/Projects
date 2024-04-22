import { useForm } from "react-hook-form";
import { useDispatch, useSelector } from "react-redux";
import {Collapse} from 'antd';
import {
  SetGroupIngredient,
  getIngredients,
} from "../../Redux/Features/AdminReqests/ingredientReqestsSlice";
import IngredientItem from "../IngredientItem/IngredientItem";
import { useEffect,useState } from "react";

function IngredientListPage() {
  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm();
  const dispatch = useDispatch();
  const ingredientList = useSelector(
    (state) => state.ingredientReqest.ingredientListDataState
  );
  const groupIngredientState = useSelector(
    (state) => state.ingredientReqest.groupIngredientState
  );
  const [updateNeeded, setUpdateNeeded] = useState(false);
  
  useEffect(() => {
    if (updateNeeded) {
      dispatch(getIngredients(groupIngredientState));
      setUpdateNeeded(false);
    }
  }, [updateNeeded, groupIngredientState, dispatch]);
  const handleDelete = () => {
    setUpdateNeeded(true);
  };
  const submit = (data) => {
    dispatch(SetGroupIngredient(data.groupIngredient))
    setUpdateNeeded(true);
  };

  const ingredientsLabels = 
    ingredientList &&
        ingredientList.map((item) =>({
          label: <span style={{ color: "black" }}>{item.name}</span>,
          key: item.id,
          children:(
            <IngredientItem
            id={item.id}
            name={item.name}
            weight={item.weight}
            nutrition={item.nutrition}
            price={item.price}
            groupIngredient={item.groupIngredient}
            onDelete ={handleDelete}
          ></IngredientItem>
          )
        }))
  

  return (
    <div>
      <form onSubmit={handleSubmit(submit)}>
        <label htmlFor="groupIngredient">Dough</label>
        <select id="groupIngredient" {...register("groupIngredient")}>
          <option value="SAUCE">SAUCE</option>
          <option value="BASIC">BASIC</option>
          <option value=" EXTRA"> EXTRA</option>
        </select>
        <input type="submit" value="Get list" />
      </form>
      <Collapse accordion style={{background:'white'}} items={ingredientsLabels}/>
      {/* {ingredientList &&
        ingredientList.map((item) => {
          return (
            <div key={item.id}>
              <IngredientItem
                id={item.id}
                name={item.name}
                weight={item.weight}
                nutrition={item.nutrition}
                price={item.price}
                groupIngredient={item.groupIngredient}
                onDelete ={handleDelete}
              ></IngredientItem>
            </div>
          );
        })} */}
    </div>
  );
}

export default IngredientListPage;
