import { useForm } from "react-hook-form";
import { useDispatch} from "react-redux";

import styles from './authorizationForm.module.css'
import { LoginReqest } from "../../Redux/Features/userReqests/userAuthorizationSlice";


function AuthorizationForm() {
  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm();


  const dispatch = useDispatch();
  const loginRegEx = /^[a-zA-Z0-9_]{5,25}$/;
  const passwordRegex = /^[a-zA-Z0-9_]{5,15}$/;
  const submit = (data)=>{
    dispatch(LoginReqest(data))
    
  }
  
  return(
    <form className={styles.formContainer} onSubmit={handleSubmit(submit)}>
             <label className={styles.labels} htmlFor="userName">Login</label>
      <input
        placeholder="Login"
        {...register("userName", {
          required: "Login is reqired!",
          pattern: {
            value: loginRegEx,
            message:
              "loggin mut contain aleast 5 symbols and use only letters and numbers",
          },
        })}
      ></input>
      {errors.userName && errors.userName.type === "required" && (
        <span>{errors.userName.message}</span>
      )}
      {errors.userName && errors.userName.type === "pattern" && (
        <span>{errors.userName.message}</span>
      )}
      <label className={styles.labels} htmlFor="password">Password</label>
      <input
        placeholder="Password"
        {...register("password", {
          required: "Password is reqired!",
          pattern: {
            value: passwordRegex,
            message:
              "Password mut contain aleast 5 symbols and use only letters and numbers",
          },
        })}
      ></input>
      {errors.password && errors.password.type === "required" && (
        <span>{errors.password.message}</span>
      )}
      {errors.password && errors.password.type === "pattern" && (
        <span>{errors.password.message}</span>
      )}
    <input type="submit" value='Login' />
    </form>

  )

}
export default AuthorizationForm;
