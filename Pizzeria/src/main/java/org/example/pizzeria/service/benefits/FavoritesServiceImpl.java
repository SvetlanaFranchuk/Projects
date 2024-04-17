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
        UserApp user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityInPizzeriaNotFoundException("User", ErrorMessage.ENTITY_NOT_FOUND));
        Favorites favoriteEntity = favoritesRepository.findByUserApp(user)
                .orElseThrow(() -> new FavoritesExistException(ErrorMessage.FAVORITES_IS_EMPTY));
        List<Pizza> pizzas = new ArrayList<>(favoriteEntity.getPizzas());
        pizzas.removeIf(pizza -> pizza.getId().equals(pizzaId));
        favoriteEntity.setPizzas(pizzas);
        favoritesRepository.save(favoriteEntity);
    }

    @Override
    public List<PizzaResponseDto> getAllFavoritePizzaByUser(Long userId) {
        Favorites favorites = favoritesRepository.findByUserApp_Id(userId)
                .orElseThrow(() -> new FavoritesExistException(ErrorMessage.FAVORITES_IS_EMPTY));
        return favorites.getPizzas().stream()
                .map(p -> pizzaMapper.toPizzaResponseDto(p)).toList();
    }

}
