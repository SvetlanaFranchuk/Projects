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

    @Override
    @Transactional
    public DoughResponseDto update(DoughUpdateRequestDto dough, Integer id) {
        Dough existingDough = doughRepository.findById(id)
                .orElseThrow(() -> new EntityInPizzeriaNotFoundException("Dough", ErrorMessage.ENTITY_NOT_FOUND));
        existingDough.setSmallWeight(dough.smallWeight());
        existingDough.setSmallNutrition(dough.smallNutrition());
        return doughMapper.toDoughResponseDto(doughRepository.save(existingDough));
    }

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

    @Override
    public List<DoughResponseDto> getAllForAdmin() {
        List<Dough> doughs = doughRepository.findAll();
        return doughs.stream()
                .map(dough -> doughMapper.toDoughResponseDto(dough)).toList();
    }

    @Override
    public List<DoughResponseClientDto> getAllForClient() {
        List<Dough> doughs = doughRepository.findAll();
        return doughs.stream()
                .map(dough -> doughMapper.toDoughResponseClientDto(dough)).toList();
    }
}
