import React from "react";
import styles from "./index.module.css";
import { BrowserRouter as Router, Route } from "react-router-dom";

import {
  RegistrationForm,
  AuthorizationForm,
  DouthReqestForm,
  IngredientForm,
  IngredientListPage,
  PizzaForm,
  GetReviev,
  PizzaListPage,
} from "../../utils";

const HomePage = () => {
  return (
    <div className={styles.mainContainer}>
      <h1>Pizza Craft</h1>
      <div className={styles.formContainer}>
        <AuthorizationForm></AuthorizationForm>
      </div>
    </div>
  );
};
export default HomePage;
