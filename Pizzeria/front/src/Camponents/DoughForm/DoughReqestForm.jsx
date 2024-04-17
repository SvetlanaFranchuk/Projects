import { useForm } from "react-hook-form";
import { useSelector, useDispatch } from "react-redux";
import {Collapse} from 'antd';
import {
  doughReqest,
  getAllDough,
} from "../../Redux/Features/AdminReqests/doughReqest";
import DoughListItem from "../DoghListItem/DoughListItem";
import { useEffect } from "react";

function DoughReqestForm() {
  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm();
  const doughData = useSelector((state) => state.doughReqest.allDoughDataState);
  console.log(doughData)
  const dougLabels = doughData &&
  doughData.map((item) =>({
    label: <span style={{ color: "black" }}>{item.typeDough}</span>,
    key: item.id,
    children:(
      <DoughListItem
      id={item.id}
        key={item.id}
        typeDough={item.typeDough}
        smallWeight={item.smallWeight}
        smallNutrion={item.smallNutrition}
        smallPrice={item.smallPrice}
      ></DoughListItem>
    )
  }))
  const dispatch = useDispatch();
 
  useEffect(() => {
    dispatch(getAllDough());
    
  }, [dispatch]);
  const submit = (data) => {
    dispatch(doughReqest(data))
    .then(()=>{
      dispatch(getAllDough())
    })
  };

  return (
    <>
      <form onSubmit={handleSubmit(submit)}>
        <label htmlFor="typeDough">Dough</label>
        <select id="typeDough" {...register("typeDough")}>
          <option value="CLASSICA">CLASSICA</option>
          <option value="PAN_PIZZA">PAN_PIZZA</option>
          <option value="SICILIAN">SICILIAN</option>
          <option value="NEW_YORK_STYLE">NEW_YORK_STYLE</option>
          <option value="NEAPOLITAN">NEAPOLITAN</option>
          <option value="WHOLE_WHEAT_FLOUR">WHOLE_WHEAT_FLOUR</option>
          <option value="CORNMEAL">CORNMEAL</option>
        </select>
        <label htmlFor="smallWeight">Weight</label>
        <input
          type="number"
          {...register("smallWeight", {
            required: "please fill the input",
            min: { value: 0, message: "min value is 0" },
            max: { value: 200, message: "max value is 200" },
          })}
        />
        {errors.smallWeight && <span>{errors.smallWeight.message}</span>}
        <label htmlFor="smallNutrition">Nutrion</label>
        <input
          type="number"
          {...register("smallNutrition", {
            required: "please fill the input",
            min: { value: 0, message: "min value is 0" },
            max: { value: 600, message: "max value is 600" },
          })}
        />
        {errors.smallNutrition && <span>{errors.smallNutrition.message}</span>}
        <label htmlFor="smallPrice">Price</label>
        <input
          type="number"
          step="0.01"
          {...register("smallPrice", {
            required: "please fill the input",
            min: { value: 0, message: "min value is 0" },
          })}
        />
        {errors.smallPrice && <span>{errors.smallPrice.message}</span>}
        <input type="submit" value="send" />
      </form>
      <div className="doughList">
      <Collapse accordion style={{background:'white'}} items={dougLabels}/>
      </div>
    </>
  );
}
export default DoughReqestForm;
