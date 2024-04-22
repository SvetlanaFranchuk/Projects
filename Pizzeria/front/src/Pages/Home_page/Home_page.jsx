import React from "react";
import styles from "./index.module.css";

import {
  AuthorizationForm,
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
