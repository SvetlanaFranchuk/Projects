import { useEffect } from "react";
import {useNavigate} from 'react-router-dom'
import Select from "react-select";
import { useDispatch, useSelector } from "react-redux";
import {v4 as uuidv4} from 'uuid';
import styles from './pizzaFormStyles.module.css'
import {
  getSauce,
  getBasic,
  getExtra,
} from "../../Redux/Features/AdminReqests/ingredientReqestsSlice";
import { sizeArray, toppingFillingsArr, stylesArr } from "../../utils";
import { getAllDough } from "../../Redux/Features/AdminReqests/doughReqest";
import { Controller, useForm } from "react-hook-form";
import { useState } from "react";
import { updatePizza,deletePizza } from "../../Redux/Features/PizzaReqest/pizzaReqestSlice";

function UpdatePizzaForm({
  id,
  title,
  description,
  styles,
  toppingsFillings,
  size,
  doughId,
  ingredientsList
}) {
  const {
    register,
    handleSubmit,
    formState: { errors },
    control,
  } = useForm();
  const ingredientsSauceListId = ingredientsList&&ingredientsList.filter(item=>item.groupIngredient==='SAUCE')
  .map((item) => ({ key:uuidv4(), value:item.id, label:item.name}));
  const ingredientsExtraListId = ingredientsList&&ingredientsList.filter(item=>item.groupIngredient==='EXTRA')
  .map((item) => ({ key:uuidv4(), value:item.id, label:item.name}));
  const ingredientsBasicListId = ingredientsList&&ingredientsList.filter(item=>item.groupIngredient==='BASIC')
  .map((item) => ({ key:uuidv4(), value:item.id, label:item.name}));
  const [sauceOptions, setSauceOptions] = useState(ingredientsSauceListId);   
  const [extraOptions, setExtraOptions] = useState(ingredientsExtraListId);
  const [basicOptions, setBasicOptions] = useState(ingredientsBasicListId);
  console.log(id)
  const navigate = useNavigate();
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
  const userId = id;
  const submit = (data) => {
    data && dispatch(updatePizza({ id: userId, data }));
    
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
  const handleDelete = (id)=>{
    console.log(id)
    dispatch(deletePizza(id));
    window.location.reload()
  }
  return (
    <div className={styles.pizzaItemContainer}>
      <form onSubmit={handleSubmit(submit)}>
        <label htmlFor="title">Title</label>
        <input
          type="text"
          defaultValue={title}
          {...register("title", {
            min: { value: 5, message: "minimum 5 symbols" },
            max: { value: 35, message: "not more then 35 symbols" },
          })}
        ></input>
        {errors.title && <span>{errors.title.message}</span>}
        <label htmlFor="description">Description</label>
        <input
          type="text"
          defaultValue={description}
          {...register("description", {
            max: { value: 255, message: "not more then 225 symbols" },
          })}
        ></input>
        {errors.description && <span>{errors.description.message}</span>}
        <label htmlFor="styles">Styles</label>
        <Controller
          control={control}
          name="styles"
          defaultValue={styles}
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
        <label htmlFor="toppingsFillings">Toppings</label>
        <Controller
          control={control}
          name="toppingsFillings"
          defaultValue={toppingsFillings}
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
        <label htmlFor="size">Size</label>
        <Controller
          control={control}
          name="size"
          defaultValue={size}
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
        <label htmlFor="ingredientsSauceListId">Sauce</label>
        <Controller
          control={control}
          name="ingredientsSauceListId"
          defaultValue={ingredientsSauceListId&&ingredientsSauceListId.map(item=>item.value)}
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
              value={sauceOptions||[]}
              isMulti
              onChange={(selected) => {
                field.onChange(handleChange(selected));
              }}
              placeholder="Choose a sauce"
            />
          )}
        />
        <label htmlFor="ingredientsExtraListId">Extra</label>
        <Controller
          control={control}
          name="ingredientsExtraListId"
          defaultValue={ingredientsExtraListId&&ingredientsExtraListId.map(item=>item.value)}
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
              value={extraOptions||[]}
              isMulti
              onChange={(selected) => {
                field.onChange(handleExtraChange(selected));
              }}
              placeholder="Choose a extra"
            />
          )}
        />
        <label htmlFor="ingredientsBasicListId">Basic</label>
        <Controller
          control={control}
          name="ingredientsBasicListId"
          defaultValue={ingredientsBasicListId&&ingredientsBasicListId.map(item=>item.value)}
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
              value={basicOptions||[]}
              isMulti
              onChange={(selected) => {
                field.onChange(handleBasicChange(selected));
              }}
              placeholder="Choose a basic"
            />
          )}
        />
        <label htmlFor="doughId">Dough</label>
        <Controller
          control={control}
          name="doughId"
          defaultValue={doughId.id}
          render={({ field }) => (
            <select
              {...field}
              onChange={(e) => field.onChange(e.target.value)}
              value={field.value}
            >
              <option value=''>{doughId.typeDough}</option>
              {doughData &&
                doughData.map((dough) => (
                  <option key={uuidv4()} value={dough.id}>
                    {dough.typeDough}
                  </option>
                ))}
            </select>

          )}
        />

        <input type="submit" />

        
      </form>
      <button onClick={()=>handleDelete(id)}>Delete Pizza</button>
    </div>
  );
}
export default UpdatePizzaForm;
