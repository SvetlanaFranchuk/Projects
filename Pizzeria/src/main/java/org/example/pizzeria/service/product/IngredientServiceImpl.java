package org.example.pizzeria.service.product;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import org.example.pizzeria.dto.product.ingredient.IngredientRequestDto;
import org.example.pizzeria.dto.product.ingredient.IngredientResponseClientDto;
import org.example.pizzeria.dto.product.ingredient.IngredientResponseDto;
import org.example.pizzeria.entity.product.ingredient.GroupIngredient;
import org.example.pizzeria.entity.product.ingredient.Ingredient;
import org.example.pizzeria.entity.product.pizza.Pizza;
import org.example.pizzeria.exception.EntityInPizzeriaNotFoundException;
import org.example.pizzeria.exception.ErrorMessage;
import org.example.pizzeria.exception.InvalidIDException;
import org.example.pizzeria.exception.product.DeleteProductException;
import org.example.pizzeria.exception.product.IngredientsCreateException;
import org.example.pizzeria.mapper.product.IngredientMapper;
import org.example.pizzeria.repository.product.IngredientRepository;
import org.example.pizzeria.repository.product.PizzaRepository;
import org.example.pizzeria.repository.user.UserRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;

@Service
public class IngredientServiceImpl implements IngredientService {
    public EntityManager entityManager;
    public IngredientRepository ingredientRepository;
    public IngredientMapper ingredientMapper;
    public PizzaRepository pizzaRepository;
    public UserRepository userRepository;

    @Autowired
    public IngredientServiceImpl(IngredientRepository ingredientRepository, IngredientMapper ingredientMapper,
                                 PizzaRepository pizzaRepository, UserRepository userRepository, EntityManager entityManager) {
        this.ingredientRepository = ingredientRepository;
        this.ingredientMapper = ingredientMapper;
        this.pizzaRepository = pizzaRepository;
        this.userRepository = userRepository;
        this.entityManager = entityManager;
    }

    @Override
    @Transactional
    public IngredientResponseDto add(IngredientRequestDto newIngredient) {
        validateIngredientExist(newIngredient.name(), newIngredient.price());
        Ingredient ingredient = ingredientMapper.toIngredient(newIngredient.name(),
                newIngredient.weight(), newIngredient.nutrition(),
                newIngredient.price(), newIngredient.groupIngredient(), new HashSet<>());
        return ingredientMapper.toIngredientResponseDto(ingredientRepository.save(ingredient));
    }

    private void validateIngredientExist(String name, double price) {
        List<Ingredient> ingredients = ingredientRepository.findAllByNameAndPrice(name, price);
        if (!ingredients.isEmpty()) {
            throw new IngredientsCreateException(ErrorMessage.INGREDIENT_ALREADY_EXIST);
        }
    }

    @Override
    @Transactional
    public IngredientResponseDto update(IngredientRequestDto ingredient, Long id) {
        Ingredient existingIngredient = ingredientRepository.findById(id)
                .orElseThrow(() -> new EntityInPizzeriaNotFoundException("Ingredient", ErrorMessage.ENTITY_NOT_FOUND));
        BeanUtils.copyProperties(ingredient, existingIngredient);
        ingredientRepository.save(existingIngredient);
        return ingredientMapper.toIngredientResponseDto(existingIngredient);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Ingredient existingIngredient = ingredientRepository.findById(id)
                .orElseThrow(() -> new InvalidIDException(ErrorMessage.INVALID_ID));

        List<Pizza> pizzasUsingIngredient = pizzaRepository.findAllByIngredientsListContaining(existingIngredient);
        if (pizzasUsingIngredient.isEmpty()) {
            ingredientRepository.delete(existingIngredient);
        } else {
            throw new DeleteProductException(ErrorMessage.INGREDIENT_ALREADY_USE_IN_PIZZA);
        }
    }

    @Override
    public List<IngredientResponseDto> getAllForAdmin() {
        List<Ingredient> ingredients = ingredientRepository.findAll();
        return ingredients.stream()
                .map(ingredient -> ingredientMapper.toIngredientResponseDto(ingredient)).toList();
    }

    @Override
    public List<IngredientResponseDto> getAllByGroup(GroupIngredient groupIngredient) {
        List<Ingredient> ingredients = ingredientRepository.findAllByGroupIngredient(groupIngredient);
        return ingredients.stream()
                .map(ingredient -> ingredientMapper.toIngredientResponseDto(ingredient)).toList();
    }

    @Override
    public List<IngredientResponseClientDto> getAllForPizza(Long idPizza) {
        try {
            Pizza pizza = pizzaRepository.getReferenceById(idPizza);
            List<Ingredient> ingredients = pizza.getIngredientsList();
            return ingredients.stream()
                    .map(ingredient -> ingredientMapper.toIngredientResponseClientDto(ingredient)).toList();
        } catch (EntityNotFoundException ex) {
            throw new EntityInPizzeriaNotFoundException("Pizza", ErrorMessage.ENTITY_NOT_FOUND);
        }
    }

}
