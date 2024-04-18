package org.example.pizzeria.service.product;

import jakarta.persistence.EntityManager;
import org.example.pizzeria.dto.product.pizza.PizzaRequestDto;
import org.example.pizzeria.dto.product.pizza.PizzaResponseDto;
import org.example.pizzeria.entity.order.OrderDetails;
import org.example.pizzeria.entity.product.ingredient.Dough;
import org.example.pizzeria.entity.product.ingredient.Ingredient;
import org.example.pizzeria.entity.product.pizza.Pizza;
import org.example.pizzeria.entity.product.pizza.Styles;
import org.example.pizzeria.entity.product.pizza.ToppingsFillings;
import org.example.pizzeria.entity.user.Role;
import org.example.pizzeria.exception.ErrorMessage;
import org.example.pizzeria.exception.InvalidIDException;
import org.example.pizzeria.exception.product.DeleteProductException;
import org.example.pizzeria.mapper.product.DoughMapper;
import org.example.pizzeria.mapper.product.IngredientMapper;
import org.example.pizzeria.mapper.product.PizzaMapper;
import org.example.pizzeria.repository.order.OrderDetailsRepository;
import org.example.pizzeria.repository.product.DoughRepository;
import org.example.pizzeria.repository.product.IngredientRepository;
import org.example.pizzeria.repository.product.PizzaRepository;
import org.example.pizzeria.repository.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PizzaServiceImpl implements PizzaService {
    public EntityManager entityManager;

    public PizzaRepository pizzaRepository;
    public PizzaMapper pizzaMapper;
    public OrderDetailsRepository detailsRepository;

    public UserRepository userRepository;

    public DoughMapper doughMapper;
    public IngredientMapper ingredientMapper;
    public DoughRepository doughRepository;
    public IngredientRepository ingredientRepository;

    @Autowired
    public PizzaServiceImpl(PizzaRepository pizzaRepository, PizzaMapper pizzaMapper,
                            OrderDetailsRepository detailsRepository, UserRepository userRepository,
                            EntityManager entityManager, DoughMapper doughMapper,
                            IngredientMapper ingredientMapper, DoughRepository doughRepository,
                            IngredientRepository ingredientRepository) {
        this.pizzaRepository = pizzaRepository;
        this.pizzaMapper = pizzaMapper;
        this.detailsRepository = detailsRepository;
        this.userRepository = userRepository;
        this.entityManager = entityManager;
        this.doughMapper = doughMapper;
        this.ingredientMapper = ingredientMapper;
        this.doughRepository = doughRepository;
        this.ingredientRepository = ingredientRepository;
    }

    @Override
    @Transactional
    public PizzaResponseDto add(PizzaRequestDto newPizza, Long userId) {
        if (userId == null) {
            throw new NullPointerException("ID cannot be null");
        }
        Pizza pizza = pizzaMapper.toPizza(newPizza);
        putIngredientToPizza(pizza, newPizza);
        pizza.setStandardRecipe(!userRepository.getReferenceById(userId).getRole().equals(Role.ROLE_CLIENT));
        pizza = pizzaRepository.save(pizza);

        PizzaResponseDto result = pizzaMapper.toPizzaResponseDto(pizza);
        result.setDough(doughMapper.toDoughResponseClientDto(pizza.getDough()));
        result.setIngredientsList(pizza.getIngredientsList().stream()
                .map(i -> ingredientMapper.toIngredientResponseClientDto(i))
                .toList());
        return result;
    }

    private void putIngredientToPizza(Pizza pizza, PizzaRequestDto newPizza) {
        List<Ingredient> ingredients = new ArrayList<>();
        addIngredientsToList(ingredients, newPizza.ingredientsSauceListId());
        addIngredientsToList(ingredients, newPizza.ingredientsBasicListId());
        addIngredientsToList(ingredients, newPizza.ingredientsExtraListId());

        Dough dough = doughRepository.getReferenceById(newPizza.doughId());
        pizza.setDough(dough);
        pizza.setAmount(getAmountPizza(ingredients, dough, newPizza.size().getCoefficient()));
        pizza.setNutrition(getNutritionPizza(ingredients, dough, newPizza.size().getCoefficient()));
        pizza.setIngredientsList(ingredients);
        for (Ingredient ingredient : ingredients) {
            if (ingredient.getPizzaSet() == null) {
                ingredient.setPizzaSet(new HashSet<>());
            }
            ingredient.getPizzaSet().add(pizza);
        }
    }

    private double getAmountPizza(List<Ingredient> ingredients, Dough dough, double coefficient) {
        double amount = 0;
        for (Ingredient ingredient : ingredients) {
            amount += ingredient.getPrice();
        }
        amount += dough.getSmallPrice();
        return amount * coefficient * 1.3;
    }

    private int getNutritionPizza(List<Ingredient> ingredients, Dough dough, double coefficient) {
        int nutrition = 0;
        for (Ingredient ingredient : ingredients) {
            nutrition += ingredient.getNutrition();
        }
        nutrition += dough.getSmallNutrition();
        return Math.toIntExact(Math.round(nutrition * coefficient));
    }

    private void addIngredientsToList(List<Ingredient> ingredients, List<Long> ingredientListId) {
        if (!ingredientListId.isEmpty()) {
            List<Ingredient> referencedIngredients = ingredientListId.stream()
                    .map(i -> ingredientRepository.getReferenceById(i))
                    .toList();
            ingredients.addAll(referencedIngredients);
        }
    }

    @Override
    @Transactional
    public PizzaResponseDto update(PizzaRequestDto pizzaDto, Long id) {
        id = Optional.ofNullable(id)
                .orElseThrow(() -> new NullPointerException("ID cannot be null"));
        Pizza existingPizza = pizzaRepository.findById(id)
                .orElseThrow(() -> new InvalidIDException(ErrorMessage.INVALID_ID));
        List<Ingredient> existingIngredients = existingPizza.getIngredientsList();
        if (existingIngredients != null && !existingIngredients.isEmpty()) {
            for (Ingredient ingredient : existingIngredients) {
                if (ingredient.getPizzaSet() != null) {
                    ingredient.getPizzaSet().remove(existingPizza);
                }
            }
        }
        existingPizza.setIngredientsList(new ArrayList<>());
        entityManager.flush();
        existingPizza.setTitle(pizzaDto.title());
        existingPizza.setDescription(pizzaDto.description());
        existingPizza.setStyles(pizzaDto.styles());
        existingPizza.setToppingsFillings(pizzaDto.toppingsFillings());
        existingPizza.setSize(pizzaDto.size());

        putIngredientToPizza(existingPizza, pizzaDto);

        Pizza updatedPizza = pizzaRepository.save(existingPizza);
        PizzaResponseDto result = pizzaMapper.toPizzaResponseDto(updatedPizza);
        result.setDough(doughMapper.toDoughResponseClientDto(updatedPizza.getDough()));
        result.setIngredientsList(updatedPizza.getIngredientsList().stream()
                .map(ingredientMapper::toIngredientResponseClientDto)
                .collect(Collectors.toList()));
        return result;
    }

    @Override
    @Transactional
    public void deletePizzaRecipe(Long id) {
        if (id == null) {
            throw new NullPointerException("ID cannot be null");
        }
        Optional<Pizza> pizza = pizzaRepository.findById(id);
        if (pizza.isPresent()) {
            List<OrderDetails> detailsList = detailsRepository.findAllByPizza(pizza.get());
            if (detailsList.isEmpty())
                pizzaRepository.delete(pizza.get());
            else throw new DeleteProductException(ErrorMessage.RECIPE_ALREADY_ORDERED);
        } else throw new InvalidIDException(ErrorMessage.INVALID_ID);
    }

    @Override
    public List<PizzaResponseDto> getAllPizzaStandardRecipe() {
        List<Pizza> pizzas = pizzaRepository.findAllByStandardRecipe(true);
        return pizzas.stream()
                .map(p -> pizzaMapper.toPizzaResponseDto(p))
                .toList();
    }

    @Override
    public List<PizzaResponseDto> getAllPizzaStandardRecipeByStyles(Styles styles) {
        List<Pizza> pizzas = pizzaRepository.findAllByStandardRecipeAndStyles(true, styles);
        return pizzas.stream()
                .map(p -> pizzaMapper.toPizzaResponseDto(p))
                .toList();
    }

    @Override
    public List<PizzaResponseDto> getAllPizzaStandardRecipeByTopping(ToppingsFillings toppingsFillings) {
        List<Pizza> pizzas = pizzaRepository.findAllByStandardRecipeAndToppingsFillings(true, toppingsFillings);
        return pizzas.stream()
                .map(p -> pizzaMapper.toPizzaResponseDto(p))
                .toList();
    }

    @Override
    public List<PizzaResponseDto> getAllPizzaStandardRecipeByToppingByStyles(ToppingsFillings toppingsFillings,
                                                                             Styles styles) {
        List<Pizza> pizzas = pizzaRepository.findAllByStandardRecipeAndToppingsFillingsAndStyles(true, toppingsFillings, styles);
        return pizzas.stream()
                .map(p -> pizzaMapper.toPizzaResponseDto(p))
                .toList();
    }

    @Override
    public PizzaResponseDto getPizza(Long id) {
        return pizzaMapper.toPizzaResponseDto(pizzaRepository.getReferenceById(id));
    }
}
