package org.example.pizzeria.service.product;

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
import org.example.pizzeria.exception.product.DeleteProductException;
import org.example.pizzeria.exception.product.DoughCreateException;
import org.example.pizzeria.exception.product.FavoritesExistException;
import org.example.pizzeria.exception.product.IngredientsCreateException;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class ProductServiceImpl implements ProductService {

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
    public ProductServiceImpl(DoughRepository doughRepository, DoughMapper doughMapper, IngredientRepository ingredientRepository, IngredientMapper ingredientMapper, PizzaRepository pizzaRepository, PizzaMapper pizzaMapper, OrderDetailsRepository detailsRepository, FavoritesRepository favoritesRepository, FavoritesMapper favoritesMapper, UserRepository userRepository) {
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
    public List<IngredientResponseClientDto> getAllIngredientByGroup(GroupIngredient groupIngredient) {
        List<Ingredient> ingredients = ingredientRepository.findAllByGroupIngredient(groupIngredient);
        return ingredients.stream()
                .map(ingredient -> ingredientMapper.toIngredientResponseClientDto(ingredient)).toList();
    }

    @Override
    public List<IngredientResponseClientDto> getAllIngredientForPizza(Long idPizza) {
        if (idPizza == null) {
            throw new NullPointerException("ID cannot be null");
        }
        Pizza pizza = pizzaRepository.getReferenceById(idPizza);
        List<Ingredient> ingredients = pizza.getIngredientsList();
        return ingredients.stream()
                .map(ingredient -> ingredientMapper.toIngredientResponseClientDto(ingredient)).toList();
    }

    @Override
    @Transactional
    public PizzaResponseDto addPizza(PizzaRequestDto newPizza, Long userId) {
        if (userId == null) {
            throw new NullPointerException("ID cannot be null");
        }
        List<Ingredient> ingredients = new ArrayList<>();
        addIngredientsToList(ingredients, newPizza.ingredientsSauceList());
        addIngredientsToList(ingredients, newPizza.ingredientsBasicList());
        addIngredientsToList(ingredients, newPizza.ingredientsExtraList());
        Dough dough = doughRepository.getReferenceById(newPizza.dough().id());
        Pizza pizza = pizzaMapper.toPizza(newPizza);
        pizza.setStandardRecipe(!userRepository.getReferenceById(userId).getRole().equals(Role.CLIENT));
        pizza.setAmount(getAmountPizza(ingredients, dough, newPizza.size().getCoefficient()));
        pizza.setNutrition(getNutritionPizza(ingredients, dough, newPizza.size().getCoefficient()));
        pizza.setIngredientsList(ingredients);
        return pizzaMapper.toPizzaResponseDto(pizzaRepository.save(pizza));
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

    private void addIngredientsToList(List<Ingredient> ingredients, List<IngredientResponseClientDto> ingredientDtoList) {
        if (!ingredientDtoList.isEmpty()) {
            ingredients.addAll(ingredientDtoList.stream()
                    .filter(Objects::nonNull)
                    .map(i -> ingredientRepository.getReferenceById(i.id()))
                    .toList());
        }
    }

    @Override
    @Transactional
    public PizzaResponseDto updatePizza(PizzaRequestDto pizza, Long id) {
        if (id == null) {
            throw new NullPointerException("ID cannot be null");
        }
        Optional<Pizza> oldPizzaRecipe = pizzaRepository.findById(id);
        if (oldPizzaRecipe.isPresent()) {
            List<Ingredient> ingredients = new ArrayList<>();
            addIngredientsToList(ingredients, pizza.ingredientsSauceList());
            addIngredientsToList(ingredients, pizza.ingredientsBasicList());
            addIngredientsToList(ingredients, pizza.ingredientsExtraList());
            Dough dough = doughRepository.getReferenceById(pizza.dough().id());

            oldPizzaRecipe.ifPresent(p -> {
                p.setTitle(pizza.title());
                p.setDescription(pizza.description());
                p.setStyles(pizza.styles());
                p.setToppingsFillings(pizza.toppingsFillings());
                p.setSize(pizza.size());
                p.setDough(dough);
                p.setIngredientsList(ingredients);
                p.setAmount(getAmountPizza(ingredients, dough, pizza.size().getCoefficient()));
                p.setNutrition(getNutritionPizza(ingredients, dough, pizza.size().getCoefficient()));

                pizzaRepository.save(p);
            });
            return oldPizzaRecipe.map(p ->
                    pizzaMapper.toPizzaResponseDto(p)).orElse(null);
        } else {
            throw new InvalidIDException(ErrorMessage.INVALID_ID);
        }
    }

    @Override
    @Transactional
    public void deletePizzaRecipe(Long id) {
        if (id == null) {
            throw new NullPointerException("ID cannot be null");
        }
        Optional<Pizza> pizza = pizzaRepository.findById(id);
        if (pizza.isPresent()) {
            List<OrderDetails> detailsList = detailsRepository.findAllByPizzasContaining(pizza.get());
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
    public FavoritesResponseDto addPizzaToUserFavorite(Long userId, Pizza pizza) {
        Optional<UserApp> user = userRepository.findById(userId);
        if (user.isPresent()) {
            Optional<Favorites> favorites = favoritesRepository.findByUserApp(user.get());
            Favorites favoriteEntity;
            if (favorites.isPresent()) {
                favoriteEntity = favorites.get();
            } else {
                favoriteEntity = new Favorites();
                favoriteEntity.setUserApp(user.get());
                favoriteEntity.setPizzas(new ArrayList<>());
            }
            List<Pizza> pizzas = new ArrayList<>(favoriteEntity.getPizzas());
            pizzas.add(pizza);
            favoriteEntity.setPizzas(pizzas);
            favoritesRepository.save(favoriteEntity);

            return favoritesMapper.toFavoriteResponseDto(favoriteEntity);
        } else {
            throw new EntityInPizzeriaNotFoundException("User", ErrorMessage.ENTITY_NOT_FOUND);
        }
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

}
