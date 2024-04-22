package org.example.pizzeria.service.benefits;

import jakarta.persistence.EntityManager;
import org.example.pizzeria.dto.benefits.FavoritesResponseDto;
import org.example.pizzeria.dto.product.pizza.PizzaResponseDto;
import org.example.pizzeria.entity.benefits.Favorites;
import org.example.pizzeria.entity.product.pizza.Pizza;
import org.example.pizzeria.entity.user.UserApp;
import org.example.pizzeria.exception.EntityInPizzeriaNotFoundException;
import org.example.pizzeria.exception.ErrorMessage;
import org.example.pizzeria.exception.product.FavoritesExistException;
import org.example.pizzeria.exception.product.PizzaAlreadyInFavoritesException;
import org.example.pizzeria.mapper.benefits.FavoritesMapper;
import org.example.pizzeria.mapper.product.PizzaMapper;
import org.example.pizzeria.repository.benefits.FavoritesRepository;
import org.example.pizzeria.repository.product.PizzaRepository;
import org.example.pizzeria.repository.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class FavoritesServiceImpl implements FavoritesService {
    public EntityManager entityManager;
    public PizzaRepository pizzaRepository;
    public PizzaMapper pizzaMapper;
    public FavoritesRepository favoritesRepository;
    public FavoritesMapper favoritesMapper;

    public UserRepository userRepository;

    @Autowired
    public FavoritesServiceImpl(PizzaRepository pizzaRepository, FavoritesRepository favoritesRepository,
                                FavoritesMapper favoritesMapper, UserRepository userRepository,
                                PizzaMapper pizzaMapper, EntityManager entityManager) {
        this.pizzaRepository = pizzaRepository;
        this.favoritesRepository = favoritesRepository;
        this.favoritesMapper = favoritesMapper;
        this.userRepository = userRepository;
        this.entityManager = entityManager;
        this.pizzaMapper = pizzaMapper;
    }
    /**
     * Adds a pizza to the user's favorites list
     * *
     * This method adds a pizza to the user's favorites list. It retrieves the user entity using the provided
     * user ID and checks if the user already has a favorites list. If the user doesn't have favorites, a new
     * favorites entity is created. Then, it checks if the pizza to add is already in the favorites list to
     * avoid duplicates. After ensuring the pizza is not already in favorites, it retrieves the pizza entity
     * and adds it to the favorites list. Finally, it saves the updated favorites entity and returns a DTO
     * containing the updated favorites information. If the pizza is already in the favorites list,
     * it throws a PizzaAlreadyInFavoritesException.
     *
     * @param userId  the ID of the user
     * @param pizzaId the ID of the pizza to add to favorites
     * @return a DTO containing the updated favorites information
     * @throws PizzaAlreadyInFavoritesException if the pizza is already in the user's favorites list
     */
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

    /**
     * Deletes a pizza from the user's favorites list.
     * *
     * This method deletes a pizza from the user's favorites list. It first retrieves the user entity
     * using the provided user ID and throws an EntityInPizzeriaNotFoundException if the user is not found.
     * Then, it retrieves the favorites entity associated with the user and throws a FavoritesExistException
     * if the user's favorites list is empty. After retrieving the pizzas from the favorites entity, it removes
     * the pizza with the given ID from the favorites list. Finally, it saves the updated favorites entity.
     *
     * @param pizzaId the ID of the pizza to remove from favorites
     * @param userId  the ID of the user
     * @throws EntityInPizzeriaNotFoundException if the user or favorites entity is not found
     * @throws FavoritesExistException           if the user's favorites list is empty
     */
    @Override
    @Transactional
    public void deletePizzaFromUserFavorite(Long pizzaId, Long userId) {
        UserApp user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityInPizzeriaNotFoundException("User", ErrorMessage.ENTITY_NOT_FOUND));
        Favorites favoriteEntity = favoritesRepository.findByUserApp(user)
                .orElseThrow(() -> new FavoritesExistException(ErrorMessage.FAVORITES_IS_EMPTY));
        List<Pizza> pizzas = new ArrayList<>(favoriteEntity.getPizzas());
        pizzas.removeIf(pizza -> pizza.getId().equals(pizzaId));
        favoriteEntity.setPizzas(pizzas);
        favoritesRepository.save(favoriteEntity);
    }

    /**
     * Retrieves all favorite pizzas belonging to the specified user.
     * *
     * This method retrieves all favorite pizzas belonging to the specified user. It first retrieves the
     * favorites entity associated with the user using the provided user ID and throws a FavoritesExistException
     * if the user's favorites list is empty. Then, it maps the favorite pizzas to PizzaResponseDto objects
     * and returns them as a list.
     *
     * @param userId the ID of the user
     * @return a list of PizzaResponseDto objects representing the favorite pizzas
     * @throws FavoritesExistException if the user's favorites list is empty
     */
    @Override
    public List<PizzaResponseDto> getAllFavoritePizzaByUser(Long userId) {
        Favorites favorites = favoritesRepository.findByUserApp_Id(userId)
                .orElseThrow(() -> new FavoritesExistException(ErrorMessage.FAVORITES_IS_EMPTY));
        return favorites.getPizzas().stream()
                .map(p -> pizzaMapper.toPizzaResponseDto(p)).toList();
    }

}
