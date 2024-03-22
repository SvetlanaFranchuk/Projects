package org.example.pizzeria.mapper.product;

import jakarta.annotation.Nullable;
import org.example.pizzeria.dto.product.dough.DoughResponseClientDto;
import org.example.pizzeria.dto.product.dough.DoughResponseDto;
import org.example.pizzeria.entity.product.ingredient.Dough;
import org.example.pizzeria.entity.product.ingredient.TypeDough;
import org.springframework.stereotype.Component;

@Component
public class DoughMapper {

public Dough toDough(TypeDough typeDough, int smallWeight,
                    int smallNutrition, double smallPrice){
    Dough dough = new Dough();
    dough.setTypeDough(typeDough);
    dough.setSmallWeight(smallWeight);
    dough.setSmallNutrition(smallNutrition);
    dough.setSmallPrice(smallPrice);
    return dough;
}

public DoughResponseDto toDoughResponseDto(Dough dough){
    return new DoughResponseDto(dough.getTypeDough(), dough.getSmallWeight(),
            dough.getSmallNutrition(), dough.getSmallPrice());
}

    public DoughResponseClientDto toDoughResponseClientDto(Dough dough){
        return new DoughResponseClientDto(dough.getId(), dough.getTypeDough(), dough.getSmallWeight(),
                dough.getSmallNutrition());
    }

}
