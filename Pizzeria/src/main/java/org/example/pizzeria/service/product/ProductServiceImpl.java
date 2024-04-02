package org.example.pizzeria.service.product;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import org.example.pizzeria.dto.benefits.FavoritesResponseDto;
import org.example.pizzeria.dto.product.dough.DoughCreateRequestDto;
import org.example.pizzeria.dto.product.dough.DoughResponseClientDto;
import org.example.pizzeria.dto.product.dough.DoughResponseDto;
import org.example.pizzeria.dto.product.dough.DoughUpdateRequestDto;
import org.example.pizzeria.dto.product.ingredient.IngredientRequestDto;
import org.example.pizzeria.dto.product.ingredient.IngredientResponseClientDto;
import org.example.pizzeria.dto.product.ingredient.IngredientResponseDto;
import org.example.pizzeria.dto.product.pizza.PizzaRequestDto;
import org.example.pizzeria.dto.product.pizza.PizzaResponseDto;
import org.example.pizzeria.entity.benefits.Favorites;
import org.example.pizzeria.entity.order.OrderDetails;
import org.example.pizzeria.entity.product.ingredient.Dough;
import org.example.pizzeria.entity.product.ingredient.GroupIngredient;
import org.example.pizzeria.entity.product.ingredient.Ingredient;
import org.example.pizzeria.entity.product.ingredient.TypeDough;
import org.example.pizzeria.entity.product.pizza.Pizza;
import org.example.pizzeria.entity.product.pizza.Styles;
import org.example.pizzeria.entity.product.pizza.ToppingsFillings;
import org.example.pizzeria.entity.user.Role;
import org.example.pizzeria.entity.user.UserApp;
import org.example.pizzeria.exception.EntityInPizzeriaNotFoundException;
import org.example.pizzeria.exception.ErrorMessage;
import org.example.pizzeria.exception.InvalidIDException;
import org.example.pizzeria.exception.product.*;
import org.example.pizzeria.mapper.benefits.FavoritesMapper;
import org.example.pizzeria.mapper.product.DoughMapper;
import org.example.pizzeria.mapper.product.IngredientMapper;
import org.example.pizzeria.mapper.product.PizzaMapper;
import org.example.pizzeria.repository.benefits.FavoritesRepository;
import org.example.pizzeria.repository.order.OrderDetailsRepository;
import org.example.pizzeria.repository.product.DoughRepository;
import org.example.pizzeria.repository.product.IngredientRepository;
import org.example.pizzeria.repository.product.PizzaRepository;
import org.example.pizzeria.repository.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {
public EntityManager entityManager;
    public DoughRepository doughRepository;
    public DoughMapper doughMapper;
    public IngredientRepository ingredientRepository;
    public IngredientMapper ingredientMapper;
    public PizzaRepository pizzaRepository;
    public PizzaMapper pizzaMapper;
    public OrderDetailsRepository detailsRepository;
    public FavoritesRepository favoritesRepository;
    public FavoritesMapper favoritesMapper;

    public UserRepository userRepository;

    @Autowired
    public ProductServiceImpl(DoughRepository doughRepository, DoughMapper doughMapper,
                              IngredientRepository ingredientRepository, IngredientMapper ingredientMapper,
                              PizzaRepository pizzaRepository, PizzaMapper pizzaMapper,
                              OrderDetailsRepository detailsRepository, FavoritesRepository favoritesRepository,
                              FavoritesMapper favoritesMapper, UserRepository userRepository, EntityManager entityManager) {
        this.doughRepository = doughRepository;
        this.doughMapper = doughMapper;
        this.ingredientRepository = ingredientRepository;
        this.ingredientMapper = ingredientMapper;
        this.pizzaRepository = pizzaRepository;
        this.pizzaMapper = pizzaMapper;
        this.detailsRepository = detailsRepository;
        this.favoritesRepository = favoritesRepository;
        this.favoritesMapper = favoritesMapper;
        this.userRepository = userRepository;
        this.entityManager = entityManager;
    }

    @Override
    @Transactional
    public DoughResponseDto addDough(DoughCreateRequestDto newDough) {
        validateDoughExist(newDough.typeDough(), newDough.smallPrice());
        return doughMapper.toDoughResponseDto(doughRepository.save(doughMapper.toDough(newDough)));
    }

    private void validateDoughExist(TypeDough typeDough, double price) {
        List<Dough> doughs = doughRepository.findAllByTypeDoughAndSmallPrice(typeDough, price);
        if (!doughs.isEmpty()) {
            throw new DoughCreateException(ErrorMessage.DOUGH_ALREADY_EXIST);
        }
    }

    @Override
    @Transactional
    public DoughResponseDto updateDough(DoughUpdateRequestDto dough, Integer id) {
        if (id == null) {
            throw new InvalidIDException(ErrorMessage.INVALID_ID);
        }
        Optional<Dough> oldRecipeDough = doughRepository.findById(id);
        return oldRecipeDough.map(d -> {
            d.setSmallWeight(dough.smallWeight());
            d.setSmallNutrition(dough.smallNutrition());
            doughRepository.save(d);
            return doughMapper.toDoughResponseDto(d);
        }).orElse(null);
    }

    @Override
    @Transactional
    public void deleteDough(Integer id) {
        if (id == null) {
            throw new NullPointerException("ID cannot be null");
        }
        Optional<Dough> dough = doughRepository.findById(id);
        if (dough.isPresent()) {
            List<Pizza> pizza = pizzaRepository.findAllByDoughIs(dough.get());
            if (pizza.isEmpty())
                doughRepository.delete(dough.get());
            else throw new DeleteProductException(ErrorMessage.DOUGH_ALREADY_USE_IN_PIZZA);
        } else
            throw new EntityInPizzeriaNotFoundException("Dough", ErrorMessage.ENTITY_NOT_FOUND);
    }

    @Override
    public List<DoughResponseDto> getAllDoughForAdmin() {
        List<Dough> doughs = doughRepository.findAll();
        return doughs.stream()
                .map(dough -> doughMapper.toDoughResponseDto(dough)).toList();
    }

    @Override
    public List<DoughResponseClientDto> getAllDoughForClient() {
        List<Dough> doughs = doughRepository.findAll();
        return doughs.stream()
                .map(dough -> doughMapper.toDoughResponseClientDto(dough)).toList();
    }

    @Override
    @Transactional
    public IngredientResponseDto addIngredient(IngredientRequestDto newIngredient) {
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
    public IngredientResponseDto updateIngredient(IngredientRequestDto ingredient, Long id) {
        if (id == null) {
            throw new NullPointerException("ID cannot be null");
        }
        Optional<Ingredient> oldIngredient = ingredientRepository.findById(id);
        return oldIngredient.map(i -> {
            i.setName(ingredient.name());
            i.setGroupIngredient(ingredient.groupIngredient());
            i.setWeight(ingredient.weight());
            i.setNutrition(ingredient.nutrition());
            i.setPrice(ingredient.price());
            ingredientRepository.save(i);
            return ingredientMapper.toIngredientResponseDto(i);
        }).orElse(null);
    }

    @Override
    @Transactional
    public void deleteIngredient(Long id) {
        if (id == null) {
            throw new NullPointerException("ID cannot be null");
        }
        Optional<Ingredient> ingredient = ingredientRepository.findById(id);
        if (ingredient.isPresent()) {
            List<Pizza> pizza = pizzaRepository.findAllByIngredientsListIsContaining(ingredient.get());
            if (pizza.isEmpty())
                ingredientRepository.delete(ingredient.get());
            else throw new DeleteProductException(ErrorMessage.INGREDIENT_ALREADY_USE_IN_PIZZA);
        } else throw new InvalidIDException(ErrorMessage.INVALID_ID);
    }

    @Override
    public List<IngredientResponseDto> getAllIngredientForAdmin() {
        List<Ingredient> ingredients = ingredientRepository.findAll();
        return ingredients.stream()
                .map(ingredient -> ingredientMapper.toIngredientResponseDto(ingredient)).toList();
    }

    @Override
    public List<IngredientResponseDto> getAllIngredientByGroup(GroupIngredient groupIngredient) {
        List<Ingredient> ingredients = ingredientRepository.findAllByGroupIngredient(groupIngredient);
        return ingredients.stream()
                .map(ingredient -> ingredientMapper.toIngredientResponseDto(ingredient)).toList();
    }

    @Override
    public List<IngredientResponseClientDto> getAllIngredientForPizza(Long idPizza) {
        if (idPizza == null) {
            throw new NullPointerException("ID cannot be null");
        }
        try {
            Pizza pizza = pizzaRepository.getReferenceById(idPizza);
            List<Ingredient> ingredients = pizza.getIngredientsList();
            return ingredients.stream()
                    .map(ingredient -> ingredientMapper.toIngredientResponseClientDto(ingredient)).toList();
        } catch (EntityNotFoundException ex) {
             throw new EntityInPizzeriaNotFoundException("Pizza", ErrorMessage.ENTITY_NOT_FOUND);
        }}

    @Override
    @Transactional
    public PizzaResponseDto addPizza(PizzaRequestDto newPizza, Long userId) {
        if (userId == null) {
            throw new NullPointerException("ID cannot be null");
        }
        Pizza pizza = pizzaMapper.toPizza(newPizza);
        putIngredientToPizza(pizza, newPizza);
        pizza.setStandardRecipe(!userRepository.getReferenceById(userId).getRole().equals(Role.CLIENT));
        pizza = pizzaRepository.save(pizza);

        PizzaResponseDto result = pizzaMapper.toPizzaResponseDto(pizza);
        result.setDough(doughMapper.toDoughResponseClientDto(pizza.getDough()));
        result.setIngredientsList(pizza.getIngredientsList().stream()
                .map(i->ingredientMapper.toIngredientResponseClientDto(i))
                .toList());
        return result;
    }

    private void putIngredientToPizza(Pizza pizza, PizzaRequestDto newPizza){
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
        return amount * coefficient;
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
    public PizzaResponseDto updatePizza(PizzaRequestDto pizzaDto, Long id) {
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
    @Transactional
    public FavoritesResponseDto addPizzaToUserFavorite(Long userId, Long pizzaId) {
        UserApp user = userRepository.getReferenceById(userId);
        Optional<Favorites> favorites = favoritesRepository.findByUserApp_Id(userId);
        Favorites favoriteEntity;
        if (favorites.isPresent()) {
            favoriteEntity = favorites.get();
        } else {
            favoriteEntity = new Favorites();
            favoriteEntity.setUserApp(user);
            favoriteEntity.setPizzas(new ArrayList<>());
        }

        List<Pizza> pizzas = new ArrayList<>(favoriteEntity.getPizzas());
        for (Pizza pizza : pizzas) {
            if (pizza.getId().equals(pizzaId)) {
                throw new PizzaAlreadyInFavoritesException(ErrorMessage.PIZZA_ALREADY_IN_FAVORITES);
            }
        }

        Pizza pizzaToAdd = pizzaRepository.getReferenceById(pizzaId);
        pizzas.add(pizzaToAdd);
        favoriteEntity.setPizzas(pizzas);
        favoritesRepository.save(favoriteEntity);
        return favoritesMapper.toFavoriteResponseDto(favoriteEntity);
    }

    @Override
    @Transactional
    public void deletePizzaFromUserFavorite(Long pizzaId, Long userId) {
        Optional<UserApp> user = userRepository.findById(userId);
        if (user.isPresent()) {
            Favorites favoriteEntity = favoritesRepository.findByUserApp(user.get())
                    .orElseThrow(() -> new FavoritesExistException(ErrorMessage.FAVORITES_IS_EMPTY));
            List<Pizza> pizzas = new ArrayList<>(favoriteEntity.getPizzas());
            pizzas.removeIf(pizza -> pizza.getId().equals(pizzaId));
            favoriteEntity.setPizzas(pizzas);
            favoritesRepository.save(favoriteEntity);
        } else {
            throw new EntityInPizzeriaNotFoundException("User", ErrorMessage.ENTITY_NOT_FOUND);
        }
    }

    @Override
    public List<PizzaResponseDto> getAllFavoritePizzaByUser(Long userId) {
        Favorites favorites = favoritesRepository.findByUserApp_Id(userId)
                .orElseThrow(() -> new FavoritesExistException(ErrorMessage.FAVORITES_IS_EMPTY));
        return favorites.getPizzas().stream()
                .map(p -> pizzaMapper.toPizzaResponseDto(p)).toList();
    }

    @Override
    public PizzaResponseDto getPizza(Long id){
        return pizzaMapper.toPizzaResponseDto(pizzaRepository.getReferenceById(id));
    }
}
