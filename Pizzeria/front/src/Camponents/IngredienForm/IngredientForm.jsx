import { useForm } from "react-hook-form";
import { useDispatch } from "react-redux";
import { addIngredient } from "../../Redux/Features/AdminReqests/ingredientReqestsSlice";

function IngredientForm() {
  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm();
  const dispatch = useDispatch();
  const submit = (data)=>{
    dispatch(addIngredient(data));
  }

  return (
    <div className="formContainer">
      <form onSubmit={handleSubmit(submit)}>
        <label htmlFor="name">Name</label>
        <input
          type="text"
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
        <input
          type="number"
          {...register("weight", {
            required: "please fill the input",
            min: { value: 0, message: "min value is 0" },
            max: { value: 200, message: "max value is 200" },
          })}
        />
        {errors.weight && <span>{errors.weight.message}</span>}
        <label htmlFor="nutrition">Nutrion</label>
        <input
          type="number"
          {...register("nutrition", {
            required: "please fill the input",
            min: { value: 0, message: "min value is 0" },
            max: { value: 600, message: "max value is 600" },
          })}
        />
        {errors.nutrition && <span>{errors.nutrition.message}</span>}
        <label htmlFor="price">Price</label>
        <input
          type="number"
          step="0.01"
          {...register("price", {
            required: "please fill the input",
            min: { value: 0, message: "min value is 0" },
          })}
        />
        {errors.price && <span>{errors.price.message}</span>}
        <label htmlFor="groupIngredient">Dough</label>
        <select id="groupIngredient" {...register("groupIngredient")}>
          <option value="SAUCE">SAUCE</option>
          <option value="BASIC">BASIC</option>
          <option value=" EXTRA"> EXTRA</option>
        </select>
          <input type="submit" value='send' />
      </form>
    </div>
  );
}
export default IngredientForm;
