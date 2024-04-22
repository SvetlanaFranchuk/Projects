import { Collapse } from "antd";
import {  useSelector } from "react-redux";
import UpdatePizzaForm from "../../Camponents/PizzaForms/UpdatePizzaForm";
import listStyles from "./pizzalistPage.module.css";
import PizzaGetFilter from "../../Camponents/PizzaForms/PizzaGetFilter";
import styles from "./pizzalistPage.module.css";

function PizzaListPage() {
  const pizzaList = useSelector(
    (state) => state.pizzaReqest.allStandartPizzaDataState
  );


  const udatePizzaLabels =
    pizzaList &&
    pizzaList.map((item) => ({
      label: (
        <div className={styles.pizzaLabelContainer}>
          <span style={{ color: "black" }}>{item.title}</span>
        </div>
      ),
      key: item.id,
      children: (
        <UpdatePizzaForm
          id={item.id}
          title={item.title}
          description={item.description}
          styles={item.styles}
          toppingsFillings={item.toppingsFillings}
          size={item.size}
          doughId={item.dough}
          ingredientsList={item.ingredientsList}
        ></UpdatePizzaForm>
      ),
    }));
  return (
    <div className={listStyles.listContainer}>
      <PizzaGetFilter></PizzaGetFilter>
      <div>
        <Collapse accordion style={{background:'white'}} items={udatePizzaLabels} />
      </div>
    </div>
  );
}
export default PizzaListPage;
