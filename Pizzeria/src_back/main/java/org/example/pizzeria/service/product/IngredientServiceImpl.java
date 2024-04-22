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

    /**
     * Adds a new ingredient to the database.
     * *
     * This method validates the existence of the ingredient and then creates a new ingredient entity with the provided information.
     * The new ingredient entity is then saved to the database, and its details are mapped to a response DTO.
     *
     * @param newIngredient the request DTO containing information about the new ingredient to be added
     * @return the response DTO containing information about the newly added ingredient
     */
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

    /**
     * Updates an existing ingredient in the database.
     * *
     * This method retrieves the existing ingredient from the database based on the provided ID.
     * It then copies the properties from the incoming ingredient request DTO to the existing ingredient entity.
     * The updated ingredient entity is saved to the database, and its details are mapped to a response DTO.
     *
     * @param ingredient the request DTO containing updated information about the ingredient
     * @param id         the ID of the existing ingredient to be updated
     * @return the response DTO containing information about the updated ingredient
     * @throws EntityInPizzeriaNotFoundException if the ingredient with the given ID is not found in the database
     */
    @Override
    @Transactional
    public IngredientResponseDto update(IngredientRequestDto ingredient, Long id) {
        Ingredient existingIngredient = ingredientRepository.findById(id)
                .orElseThrow(() -> new EntityInPizzeriaNotFoundException("Ingredient", ErrorMessage.ENTITY_NOT_FOUND));
        BeanUtils.copyProperties(ingredient, existingIngredient);
        ingredientRepository.save(existingIngredient);
        return ingredientMapper.toIngredientResponseDto(existingIngredient);
    }

    /**
     * Deletes an ingredient from the database.
     * *
     * This method retrieves the existing ingredient from the database based on the provided ID.
     * If there are no pizzas that use the ingredient, the ingredient is deleted from the database.
     * Otherwise, if any pizzas are using the ingredient, a DeleteProductException is thrown to indicate that the ingredient cannot be deleted.
     *
     * @param id the ID of the ingredient to be deleted
     * @throws InvalidIDException         if the provided ID is invalid or if the ingredient with the given ID is not found in the database
     * @throws DeleteProductException     if the ingredient is already used in one or more pizzas and cannot be deleted
     */
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

    /**
     * Retrieves all ingredients from the database for administrative purposes.
     * *
     * This method retrieves all ingredients stored in the database and maps them to IngredientResponseDto objects.
     *
     * @return a list of IngredientResponseDto objects representing all ingredients in the database
     */
    @Override
    public List<IngredientResponseDto> getAllForAdmin() {
        List<Ingredient> ingredients = ingredientRepository.findAll();
        return ingredients.stream()
                .map(ingredient -> ingredientMapper.toIngredientResponseDto(ingredient)).toList();
    }

    /**
     * Retrieves all ingredients from the database by group for client purposes.
     * *
     * This method retrieves all ingredients from the database that belong to the specified group and maps them to IngredientResponseDto objects.
     *
     * @param groupIngredient the group of ingredients to filter by
     * @return a list of IngredientResponseDto objects representing all ingredients in the specified group
     */
    @Override
    public List<IngredientResponseDto> getAllByGroup(GroupIngredient groupIngredient) {
        List<Ingredient> ingredients = ingredientRepository.findAllByGroupIngredient(groupIngredient);
        return ingredients.stream()
                .map(ingredient -> ingredientMapper.toIngredientResponseDto(ingredient)).toList();
    }

    /**
     * Retrieves all ingredients associated with a specific pizza for client purposes.
     * *
     * This method retrieves all ingredients associated with the specified pizza from the database and maps them to
     * IngredientResponseClientDto objects.
     *
     * @param idPizza the ID of the pizza to retrieve ingredients for
     * @return a list of IngredientResponseClientDto objects representing all ingredients associated with the specified pizza
     * @throws EntityInPizzeriaNotFoundException if the pizza with the specified ID is not found
     */
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
