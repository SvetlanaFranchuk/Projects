import { useEffect } from "react";
import Select from "react-select";
import { useDispatch, useSelector } from "react-redux";
import {
  getSauce,
  getBasic,
  getExtra,
} from "../../Redux/Features/AdminReqests/ingredientReqestsSlice";
import { sizeArray, toppingFillingsArr, stylesArr, getIdFromLocalStorage } from "../../utils";
import { getAllDough } from "../../Redux/Features/AdminReqests/doughReqest";
import { Controller, useForm } from "react-hook-form";
import { useState } from "react";
import { addPizza } from "../../Redux/Features/PizzaReqest/pizzaReqestSlice";

function PizzaForm() {
  const {
    register,
    handleSubmit,
    formState: { errors },
    control,
  } = useForm();
  const [sauceOptions, setSauceOptions] = useState([]);
  const [extraOptions, setExtraOptions] = useState([]);
  const [basicOptions, setBasicOptions] = useState([]);
  const dispatch = useDispatch();
  const { sauceDataState, extraDataState, basicDataState } = useSelector(
    (state) => state.ingredientReqest
  );
  const doughData = useSelector((state) => state.doughReqest.allDoughDataState);
  const sauceList = sauceDataState;
  const extraList = extraDataState;
  const basicList = basicDataState;
  useEffect(() => {
    dispatch(getSauce());
    dispatch(getBasic());
    dispatch(getExtra());
    dispatch(getAllDough());
  }, []);
  const userId = getIdFromLocalStorage();
  console.log(userId)
  const submit = (data) => {
   
    
    data&&dispatch(addPizza({id:userId,data}))
  };
  const handleChange = (selected) => {
    setSauceOptions(selected.slice(0, 3));
    return selected.slice(0, 3).map((option) => option.value);
  };
  const handleExtraChange = (selected) => {
    setExtraOptions(selected.slice(0, 7));
    return selected.slice(0, 7).map((option) => option.value);
  };
  const handleBasicChange = (selected) => {
    setBasicOptions(selected.slice(0, 4));
    return selected.slice(0, 4).map((option) => option.value);
  };
  
  return (
    <div>
      <form onSubmit={handleSubmit(submit)}>
      <label htmlFor="title" style={{color:'blue'}}>Title</label>
      <input type="text" defaultValue=''{...register('title',{min:{value:5,message:'minimum 5 symbols'}, max:{value:35,message:'not more then 35 symbols'}})}></input>
      {errors.title&&(<span>{errors.title.message}</span>)}
      <label htmlFor="description" style={{color:'blue'}}>Description</label>
      <input type="text" defaultValue=''{...register('description',{max:{value:255,message:'not more then 225 symbols'}})}></input>
      {errors.description&&(<span>{errors.description.message}</span>)}
      <label htmlFor="styles"style={{color:'blue'}}>Styles</label>
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
        <label htmlFor="toppingsFillings" style={{color:'blue'}}>Toppings</label>
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
        <label htmlFor="size" style={{color:'blue'}}>Size</label>
        <Controller
          control={control}
          name="size"
          render={({ field }) => (
            <select
              {...field}
              onChange={(e) => field.onChange(e.target.value)}
              value={field.value}
            >
              <option value="">Choose a size</option>
              {sizeArray &&
                sizeArray.map((item) => (
                  <option key={item.id} value={item.value}>
                    {item.value}
                  </option>
                ))}
            </select>
          )}
        />
        <label htmlFor="ingredientsSauceListId" style={{color:'blue'}}>Sauce</label>
        <Controller
          control={control}
          name="ingredientsSauceListId"
          render={({ field }) => (
            <Select
              {...field}
              options={
                sauceList &&
                sauceList.map((sauce) => ({
                  value: sauce.id,
                  label: sauce.name,
                }))
              }
              value={sauceOptions}
              isMulti
              onChange={(selected) => {
                field.onChange(handleChange(selected));
              }}
              placeholder="Choose a sauce"
            />
          )}
        />
        <label htmlFor="ingredientsExtraListId" style={{color:'blue'}}>Extra</label>
        <Controller
          control={control}
          name="ingredientsExtraListId"
          render={({ field }) => (
            <Select
              {...field}
              options={
                extraList &&
                extraList.map((sauce) => ({
                  value: sauce.id,
                  label: sauce.name,
                }))
              }
              value={extraOptions}
              isMulti
              onChange={(selected) => {
                field.onChange(handleExtraChange(selected));
              }}
              placeholder="Choose a extra"
            />
          )}
        />
        <label htmlFor="ingredientsBasicListId" style={{color:'blue'}}>Basic</label>
        <Controller
          control={control}
          name="ingredientsBasicListId"
          render={({ field }) => (
            <Select
              {...field}
              options={
                basicList &&
                basicList.map((sauce) => ({
                  value: sauce.id,
                  label: sauce.name,
                }))
              }
              value={basicOptions}
              isMulti
              onChange={(selected) => {
                field.onChange(handleBasicChange(selected));
              }}
              placeholder="Choose a basic"
            />
          )}
        />
        <label htmlFor="doughId" style={{color:'blue'}}>Dough</label>
        <Controller
          control={control}
          name="doughId"
          render={({ field }) => (
            <select
              {...field}
              onChange={(e) => field.onChange(e.target.value)}
              value={field.value}
            >
              <option value="">Choose a dough</option>
              {doughData &&
                doughData.map((dough) => (
                  <option key={dough.id} value={dough.id}>
                    {dough.typeDough}
                  </option>
                ))}
            </select>
          )}
        />

        <input type="submit" />
      </form>
    </div>
  );
}
export default PizzaForm;
