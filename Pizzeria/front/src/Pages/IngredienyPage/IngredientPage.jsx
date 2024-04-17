import { useState } from "react"
import DoughComponent from "../../Camponents/DoughComponent/DoughComponent";
import { IngredientListPage } from "../../utils";


function IngredientsPage(){

    const [isDough,setIsDough] = useState(false);
    const [isIngredients,setIsIngredients] = useState(false);

    const handleDoughInfo = ()=>{
        setIsIngredients(false);
        setIsDough(true);
    }
    const handleIngredientInfo = ()=>{
        setIsDough(false);
        setIsIngredients(true);
    }
    return(<div>
        <button onClick={handleDoughInfo}>Get Dough Info</button>
        <button onClick={handleIngredientInfo}>Get Ingredients Info</button>
        {isDough&&<DoughComponent></DoughComponent>}
        {isIngredients&&<IngredientListPage></IngredientListPage>}
    </div>)
}
export default IngredientsPage