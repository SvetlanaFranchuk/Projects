import { useForm } from "react-hook-form";
import { useDispatch,useSelector} from "react-redux";
import { updateIngredient,ingredientDelete} from "../../Redux/Features/AdminReqests/ingredientReqestsSlice";


function IngredientItem({id,name,weight,nutrition,price,groupIngredient,onDelete}) {
    const {
      register,
      handleSubmit,
      formState: { errors },
    } = useForm();
    
   
    const dispatch = useDispatch();
    const submit = (data)=>{
      
      dispatch(updateIngredient({id,data}));
    }
    const handleDelete = ()=>{
      dispatch(ingredientDelete(id))
      .then(()=>{
        onDelete();
      })
      
    }
    return (
      <div className="formContainer">
        <form onSubmit={handleSubmit(submit)}>
          <label htmlFor="name">Name</label>
          <input
            type="text" defaultValue={name}
            {...register("name", {
              required: "please fill input",
              pattern: {
                value: /^[a-zA-Z_ ]*(?:[A-Z][a-zA-Z_ ]*){0,74}$/,
                message: "incorrect data type",
              },
            })}
          ></input>
          {errors.name && <span>{errors.name.message}</span>}
          <label htmlFor="weight">Weight</label>
          <input defaultValue={weight}
            type="number"
            {...register("weight", {
              required: "please fill the input",
              min: { value: 0, message: "min value is 0" },
              max: { value: 200, message: "max value is 200" },
            })}
          />
          {errors.weight && <span>{errors.weight.message}</span>}
          <label htmlFor="nutrition">Nutrion</label>
          <input defaultValue={nutrition}
            type="number"
            {...register("nutrition", {
              required: "please fill the input",
              min: { value: 0, message: "min value is 0" },
              max: { value: 600, message: "max value is 600" },
            })}
          />
          {errors.nutrition && <span>{errors.nutrition.message}</span>}
          <label htmlFor="price">Price</label>
          <input defaultValue={price}
            type="number"
            step="0.01"
            {...register("price", {
              required: "please fill the input",
              min: { value: 0, message: "min value is 0" },
            })}
          />
          {errors.price && <span>{errors.price.message}</span>}
          <label htmlFor="groupIngredient">Dough</label>
          <select id="groupIngredient" defaultValue={groupIngredient} {...register("groupIngredient")}>
            <option value="SAUCE">SAUCE</option>
            <option value="BASIC">BASIC</option>
            <option value=" EXTRA"> EXTRA</option>
          </select>
            <input type="submit" value='update' />
            <button  onClick={handleDelete}>Delete</button>
        </form>
      </div>
    );
  }
  export default IngredientItem;