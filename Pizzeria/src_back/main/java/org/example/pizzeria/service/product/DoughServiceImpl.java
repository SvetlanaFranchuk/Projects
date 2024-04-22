package org.example.pizzeria.service.product;

import jakarta.persistence.EntityManager;
import org.example.pizzeria.dto.product.dough.DoughCreateRequestDto;
import org.example.pizzeria.dto.product.dough.DoughResponseClientDto;
import org.example.pizzeria.dto.product.dough.DoughResponseDto;
import org.example.pizzeria.dto.product.dough.DoughUpdateRequestDto;
import org.example.pizzeria.entity.product.ingredient.Dough;
import org.example.pizzeria.entity.product.ingredient.TypeDough;
import org.example.pizzeria.entity.product.pizza.Pizza;
import org.example.pizzeria.exception.EntityInPizzeriaNotFoundException;
import org.example.pizzeria.exception.ErrorMessage;
import org.example.pizzeria.exception.product.DeleteProductException;
import org.example.pizzeria.exception.product.DoughCreateException;
import org.example.pizzeria.mapper.product.DoughMapper;
import org.example.pizzeria.repository.product.DoughRepository;
import org.example.pizzeria.repository.product.PizzaRepository;
import org.example.pizzeria.repository.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DoughServiceImpl implements DoughService {
    public EntityManager entityManager;
    public DoughRepository doughRepository;
    public PizzaRepository pizzaRepository;
    public DoughMapper doughMapper;
    public UserRepository userRepository;

    @Autowired
    public DoughServiceImpl(DoughRepository doughRepository, DoughMapper doughMapper,
                            PizzaRepository pizzaRepository,
                            UserRepository userRepository, EntityManager entityManager) {
        this.doughRepository = doughRepository;
        this.doughMapper = doughMapper;
        this.userRepository = userRepository;
        this.pizzaRepository = pizzaRepository;
        this.entityManager = entityManager;
    }

    /**
     * Adds a new dough to the database and returns its details as a {@link DoughResponseDto}.
     * *
     * This method validates whether the specified dough type and small price combination already exists in the database.
     * If the combination already exists, it throws a {@link DoughCreateException}.
     *
     * @param newDough the details of the new dough to be added
     * @return the details of the newly added dough as a {@link DoughResponseDto}
     * @throws DoughCreateException if the specified dough type and small price combination already exists
     */
    @Override
    @Transactional
    public DoughResponseDto add(DoughCreateRequestDto newDough) {
        validateDoughExist(newDough.typeDough(), newDough.smallPrice());
        return doughMapper.toDoughResponseDto(doughRepository.save(doughMapper.toDough(newDough)));
    }

    private void validateDoughExist(TypeDough typeDough, double price) {
        List<Dough> doughs = doughRepository.findAllByTypeDoughAndSmallPrice(typeDough, price);
        if (!doughs.isEmpty()) {
            throw new DoughCreateException(ErrorMessage.DOUGH_ALREADY_EXIST);
        }
    }

    /**
     * Updates the details of an existing dough in the database and returns its updated details as a {@link DoughResponseDto}.
     * *
     * This method retrieves the existing dough from the database based on the specified ID. If no dough with the specified ID is found,
     * it throws an {@link EntityInPizzeriaNotFoundException}. Otherwise, it updates the small weight and small nutrition properties
     * of the existing dough with the values provided in the {@code dough} parameter and saves the changes to the database.
     *
     * @param dough the updated details of the dough
     * @param id the ID of the existing dough to be updated
     * @return the updated details of the dough as a {@link DoughResponseDto}
     * @throws EntityInPizzeriaNotFoundException if no dough with the specified ID is found
     */
    @Override
    @Transactional
    public DoughResponseDto update(DoughUpdateRequestDto dough, Integer id) {
        Dough existingDough = doughRepository.findById(id)
                .orElseThrow(() -> new EntityInPizzeriaNotFoundException("Dough", ErrorMessage.ENTITY_NOT_FOUND));
        existingDough.setSmallWeight(dough.smallWeight());
        existingDough.setSmallNutrition(dough.smallNutrition());
        return doughMapper.toDoughResponseDto(doughRepository.save(existingDough));
    }

    /**
     * Deletes a dough from the database based on the specified ID.
     * *
     * This method first checks if a dough with the specified ID exists in the database. If it exists, it retrieves all pizzas
     * associated with the dough. If there are no pizzas associated with the dough, it deletes the dough from the database.
     * Otherwise, it throws a {@link DeleteProductException} indicating that the dough is already used in pizzas.
     * *
     * If no dough with the specified ID is found in the database, it throws an {@link EntityInPizzeriaNotFoundException}.
     *
     * @param id the ID of the dough to be deleted
     * @throws DeleteProductException if the dough is already used in pizzas
     * @throws EntityInPizzeriaNotFoundException if no dough with the specified ID is found
     */
    @Override
    @Transactional
    public void delete(Integer id) {
        if (doughRepository.existsById(id)) {
            List<Pizza> pizzas = pizzaRepository.findAllByDoughId(id);
            if (pizzas.isEmpty()) {
                doughRepository.deleteById(id);
            } else {
                throw new DeleteProductException(ErrorMessage.DOUGH_ALREADY_USE_IN_PIZZA);
            }
        } else {
            throw new EntityInPizzeriaNotFoundException("Dough", ErrorMessage.ENTITY_NOT_FOUND);
        }
    }

    /**
     * Retrieves all doughs from the database for administrative purposes.
     * *
     * This method retrieves all dough entities from the database and maps them to corresponding DTOs.
     *
     * @return a list of {@link DoughResponseDto} containing information about all doughs
     */
    @Override
    public List<DoughResponseDto> getAllForAdmin() {
        List<Dough> doughs = doughRepository.findAll();
        return doughs.stream()
                .map(dough -> doughMapper.toDoughResponseDto(dough)).toList();
    }

    /**
     * Retrieves all doughs from the database for client purposes.
     * *
     * This method retrieves all dough entities from the database and maps them to simplified DTOs suitable for client-side usage.
     *
     * @return a list of {@link DoughResponseClientDto} containing information about all doughs
     */
    @Override
    public List<DoughResponseClientDto> getAllForClient() {
        List<Dough> doughs = doughRepository.findAll();
        return doughs.stream()
                .map(dough -> doughMapper.toDoughResponseClientDto(dough)).toList();
    }
}
