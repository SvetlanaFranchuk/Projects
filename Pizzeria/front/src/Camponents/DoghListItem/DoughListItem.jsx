import {useForm} from 'react-hook-form'
import { doughDelete,getAllDough,updateDough} from '../../Redux/Features/AdminReqests/doughReqest';
import {  useDispatch } from 'react-redux';
import { useEffect } from 'react';


function DoughListItem({id,typeDough,smallWeight,smallNutrion,smallPrice}){
    
  const {
    register,
    handleSubmit,
    formState: { errors },
  
  } = useForm();

  
  const dispatch = useDispatch();
  const submit = (data)=>{
    
      console.log(data)
    dispatch(updateDough({id,data}))
    .then(()=>{
      dispatch(getAllDough())
    })
  }
  const handleDelete = ()=>{
    dispatch(doughDelete(id))
    .then(()=>{
        dispatch(getAllDough())
      })
  }
    return(
        <form onSubmit={handleSubmit(submit)}>
        <p>{typeDough}</p>
  
        <label htmlFor="smallWeight">Weight</label>
        <input defaultValue={smallWeight}
          type="number"
          {...register("smallWeight", {
            required: "please fill the input",
            min: { value: 0, message: "min value is 0" },
            max: { value: 200, message: "max value is 200" },
          })}
        />
        {errors.smallWeight&&(<span>{errors.smallWeight.message}</span>)}
        <label htmlFor="smallNutrition">Nutrion</label>
        <input defaultValue={smallNutrion}
          type="number"
          {...register("smallNutrition", {
            required: "please fill the input",
            min: { value: 0, message: "min value is 0" },
            max: { value: 600, message: "max value is 600" },
          })}
        />
        {errors.smallNutrition&&(<span>{errors.smallNutrition.message}</span>)}
        <p>Amount {smallPrice}</p>
        <input type="submit" value='send' />
        <button  onClick={handleDelete}>Delete</button>
      </form>
  
    )
}
export default DoughListItem;