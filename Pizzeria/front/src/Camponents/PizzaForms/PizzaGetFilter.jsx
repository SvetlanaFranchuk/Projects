import { useForm, Controller } from "react-hook-form";
import { Select } from "antd";
import { v4 as uuidv4 } from "uuid";
import { toppingFillingsArr, stylesArr } from "../../utils";
import { useDispatch, useSelector } from "react-redux";
import {
  getPizzaByTopping,
  getPizzaByStyles,
  getStandartPizza,
  getPizzaByToppingAndStyles
} from "../../Redux/Features/PizzaReqest/pizzaReqestSlice";

function PizzaGetFilter() {
  const dispatch = useDispatch();
  const {
    handleSubmit,
    formState: { errors },
    control,
  } = useForm();
  const { Option } = Select;


  const handleToppingChange = (value) => {
    dispatch(getPizzaByTopping(value));
  };
  const handleStylesChange = (value) => {
    dispatch(getPizzaByStyles(value));
  };
  const handleAllRecipes = () => {
    dispatch(getStandartPizza());
  };
  const submit =(data)=>{
    dispatch(getPizzaByToppingAndStyles(data))
  }
  return (
    <>

      <div>
        <button onClick={handleAllRecipes}>All standart recipes</button>
        <Select defaultValue="Get by topping" onSelect={handleToppingChange}>
          {toppingFillingsArr &&
            toppingFillingsArr.map((item) => (
              <Option key={uuidv4()} value={item.value}>
                {item.value}
              </Option>
            ))}
        </Select>
        <Select defaultValue="Get by Style" onSelect={handleStylesChange}>
          {stylesArr &&
            stylesArr.map((item) => (
              <Option key={uuidv4()} value={item.value}>
                {item.value}
              </Option>
            ))}
        </Select>
      </div>
      <div>
        <form onSubmit={handleSubmit(submit)}>
          <label htmlFor="styles" style={{color:'white'}}>Styles</label>
          <Controller
            control={control}
            name="styles"
            render={({ field }) => (
              <select
                {...field}
                onChange={(e) => field.onChange(e.target.value)}
                value={field.value}
                required
              >
                <option value="">Choose a style</option>
                {stylesArr &&
                  stylesArr.map((item) => (
                    <option key={item.id} value={item.value}>
                      {item.value}
                    </option>
                  ))}
              </select>
            )}
          />
          <label htmlFor="toppingsFillings"style={{color:'white'}}>Toppings</label>
          <Controller
            control={control}
            name="toppingsFillings"
            render={({ field }) => (
              <select
                {...field}
                onChange={(e) => field.onChange(e.target.value)}
                value={field.value}
              >
                <option value="">Choose a topping</option>
                {toppingFillingsArr &&
                  toppingFillingsArr.map((item) => (
                    <option key={item.id} value={item.value}>
                      {item.value}
                    </option>
                  ))}
              </select>
            )}
          />
          <input type="submit" />
        </form>
      </div>
    </>
  );
}
export default PizzaGetFilter;
