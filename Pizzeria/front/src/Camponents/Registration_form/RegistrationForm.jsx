import { useForm } from "react-hook-form";
import styles from './registrationForm.module.css'
import { userRegReqest } from "../../Redux/Features/userReqests/userRegistrationSlice";
import {  useDispatch,useSelector} from "react-redux";

function RegistrationForm() {
  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm();
  const userResponse = useSelector((state)=>state.userReg.userregReqest)
  const dispatch = useDispatch();
  const submit = (data)=>{
    dispatch(userRegReqest(data))
    
  };
  console.log(userResponse)
  const loginRegEx = /^[a-zA-Z0-9_]{5,25}$/;
  const passwordRegex = /^[a-zA-Z0-9_]{5,15}$/;
  const emailRegex = /^[\w-]+@([\w-]+\.)+[\w-]{2,4}$/g;
  const addressNumRegex = /^\d+[-/]?[a-zA-Z]?.*$/;
  const phoneRegex = /^[(]?[0-9]{3}[)]?[-\s]?[0-9]{3}[-\s]?[0-9]{4,6}$/

  return (
    <div>
    <form className={styles.formContainer} onSubmit={handleSubmit(submit)}>
      <label className={styles.formLabel} htmlFor="userName">Login</label>
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
      <label className={styles.formLabel} htmlFor="password">Password</label>
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
      <label className={styles.formLabel} htmlFor="email">Email</label>
      <input
        placeholder="Email"
        {...register("email", {
          required: "Email is reqired!",
          pattern: {
            value: emailRegex,
            message: "Incorect input type",
          },
        })}
      ></input>
      {errors.email && errors.email.type === "required" && (
        <span>{errors.email.message}</span>
      )}
      {errors.email && errors.email.type === "pattern" && (
        <span>{errors.email.message}</span>
      )}
      <label className={styles.formLabel} htmlFor="birthDate">Birth Date</label>
      <input type="date" {...register("birthDate")} />
      <label className={styles.formLabel} htmlFor="addressCity">City</label>
      <input
        placeholder="City"
        {...register("addressCity", {
          required: "Enter your city address",
          pattern: {
            value: loginRegEx,
            message: "Incorect input type",
          },
        })}
      ></input>
      {errors.addressCity && errors.addressCity.type === "required" && (
        <span>{errors.addressCity.message}</span>
      )}
      {errors.addressCity && errors.addressCity.type === "pattern" && (
        <span>{errors.addressCity.message}</span>
      )}
      <label className={styles.formLabel} htmlFor="addressStreetName">Street</label>
      <input
        placeholder="Street"
        {...register("addressStreetName", {
          required: "Enter your street address",
          pattern: {
            value: loginRegEx,
            message: "Incorect input type",
          },
        })}
      ></input>
      {errors.addressStreetName &&
        errors.addressStreetName.type === "required" && (
          <span>{errors.addressStreetName.message}</span>
        )}
      {errors.addressStreetName &&
        errors.addressStreetName.type === "pattern" && (
          <span>{errors.addressStreetName.message}</span>
        )}
      <label className={styles.formLabel} htmlFor="addressHouseNumber">House</label>
      <input
        placeholder="House"
        {...register("addressHouseNumber", {
          required: "Email is reqired!",
          pattern: {
            value: addressNumRegex,
            message: "Incorect house number",
          },
        })}
      ></input>
      {errors.addressHouseNumber &&
        errors.addressHouseNumber.type === "required" && (
          <span>{errors.addressHouseNumber.message}</span>
        )}
      {errors.addressHouseNumber &&
        errors.addressHouseNumber.type === "pattern" && (
          <span>{errors.addressHouseNumber.message}</span>
        )}
      <label className={styles.formLabel} htmlFor="addressApartmentNumber">Appartment</label>
      <input
        placeholder="Appartment"
        {...register("addressApartmentNumber", {
          pattern: {
            value: addressNumRegex,
            message: "Incorect input type",
          },
        })}
      ></input>
      {errors.addressApartmentNumber &&
        errors.addressApartmentNumber.type === "required" && (
          <span>{errors.addressApartmentNumber.message}</span>
        )}
      {errors.addressApartmentNumber &&
        errors.addressApartmentNumber.type === "pattern" && (
          <span>{errors.addressApartmentNumber.message}</span>
        )}
      <label className={styles.formLabel} htmlFor="phoneNumber">Phone</label>
      <input
        placeholder="Phone Number"
        {...register("phoneNumber", {
          pattern: {
            value: phoneRegex,
            message: "Incorect input type",
          },
        })}
      ></input>
      {errors.addressApartmentNumber &&
        errors.addressApartmentNumber.type === "required" && (
          <span>{errors.addressApartmentNumber.message}</span>
        )}
      {errors.addressApartmentNumber &&
        errors.addressApartmentNumber.type === "pattern" && (
          <span>{errors.addressApartmentNumber.message}</span>
        )}

      <input type="submit" value='Register'/>
    </form>
    </div>
  );
}
export default RegistrationForm;
