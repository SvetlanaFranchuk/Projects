


function StandartPizzaItem({amount,ingredientsList,nutrition,size,styles,title,toppingsFillings}){

const sauceList = ingredientsList.filter(item=>item.groupIngredient==='SAUCE');
const basicList = ingredientsList.filter(item=>item.groupIngredient==='BASIC');
const extraList = ingredientsList.filter(item=>item.groupIngredient==='EXTRA');

return(
    <div>
        <span>Title</span>
        <p>{title}</p>
        <span>Topping</span>
        <p>{toppingsFillings}</p>
        <span>Styles</span>
        <p>{styles}</p>
        <span>Size</span>
        <p>{size}</p>
        <span>Nutrition</span>
        <p>{nutrition}</p>
        <span>Amount</span>
        <p>{amount}</p>
        <ul>
            <p>Sauces</p>
            {sauceList&&sauceList.map((item)=>(
                <li key={item.id}>{item.name}</li>
            ))}
        </ul>
        <ul>
            <p>Basics</p>
            {basicList&&basicList.map((item)=>(
                <li key={item.id}>{item.name}</li>
            ))}
        </ul>
        <ul>
            <p>Extras</p>
            {extraList&&extraList.map((item)=>(
                <li key={item.id}>{item.name}</li>
            ))}
        </ul>
    </div>
)

}
export default StandartPizzaItem;