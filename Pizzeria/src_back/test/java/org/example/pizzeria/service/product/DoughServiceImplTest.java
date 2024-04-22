package org.example.pizzeria.service.product;

import org.example.pizzeria.TestData;
import org.example.pizzeria.dto.product.dough.DoughResponseClientDto;
import org.example.pizzeria.dto.product.dough.DoughResponseDto;
import org.example.pizzeria.dto.product.dough.DoughUpdateRequestDto;
import org.example.pizzeria.entity.product.ingredient.Dough;
import org.example.pizzeria.entity.product.ingredient.TypeDough;
import org.example.pizzeria.entity.product.pizza.Pizza;
import org.example.pizzeria.exception.EntityInPizzeriaNotFoundException;
import org.example.pizzeria.exception.product.DeleteProductException;
import org.example.pizzeria.exception.product.DoughCreateException;
import org.example.pizzeria.mapper.product.DoughMapper;
import org.example.pizzeria.repository.product.DoughRepository;
import org.example.pizzeria.repository.product.PizzaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DoughServiceImplTest {
    @Mock
    private DoughRepository doughRepository;
    @Mock
    private PizzaRepository pizzaRepository;
    @Mock
    private DoughMapper doughMapper;
    @InjectMocks
    private DoughServiceImpl doughService;

    @BeforeEach
    void setUp() {
        Mockito.reset(doughRepository);
        Mockito.reset(pizzaRepository);
    }

    @Test
    void add() {
        when(doughMapper.toDough(TestData.DOUGH_REQUEST_DTO))
                .thenReturn(TestData.DOUGH_1);
        when(doughRepository.save(TestData.DOUGH_1)).thenReturn(TestData.DOUGH_1);
        when(doughMapper.toDoughResponseDto(TestData.DOUGH_1)).thenReturn(TestData.DOUGH_RESPONSE_DTO);

        DoughResponseDto result = doughService.add(TestData.DOUGH_REQUEST_DTO);
        assertEquals(TestData.DOUGH_RESPONSE_DTO, result);
    }

    @Test
    void add_DuplicateDoughTypeAndPrice() {
        Dough existingDough = new Dough(1, TypeDough.CLASSICA, 120, 140, 0.23);
        when(doughRepository.findAllByTypeDoughAndSmallPrice(TypeDough.CLASSICA, 0.23))
                .thenReturn(Collections.singletonList(existingDough));
        assertThrows(DoughCreateException.class, () -> doughService.add(TestData.DOUGH_REQUEST_DTO));
    }

    @Test
    void update() {
        Dough existingDough = new Dough(1, TypeDough.CORNMEAL, 130, 140, 0.23);
        Dough newDough = new Dough(1, TypeDough.CORNMEAL, 110, 110, 0.23);
        when(doughRepository.findById(1)).thenReturn(Optional.of(existingDough));
        when(doughRepository.save(newDough)).thenReturn(newDough);
        when(doughMapper.toDoughResponseDto(newDough)).thenReturn(TestData.DOUGH_RESPONSE_DTO_NEW);
        DoughResponseDto responseDto = doughService.update(TestData.DOUGH_UPDATE_REQUEST_DTO, 1);
        assertEquals(TestData.DOUGH_RESPONSE_DTO_NEW, responseDto);
    }

    @Test
    void update_InvalidID() {
        DoughUpdateRequestDto updatedDough = new DoughUpdateRequestDto(1, 110, 110);
        assertThrows(EntityInPizzeriaNotFoundException.class, () -> doughService.update(updatedDough, null));
    }

    @Test
    void delete() {
        Integer doughId = 1;
        when(doughRepository.existsById(doughId)).thenReturn(true);
        when(pizzaRepository.findAllByDoughId(doughId)).thenReturn(Collections.emptyList());
        doughService.delete(doughId);
        verify(doughRepository).deleteById(doughId);
    }

    @Test
    void delete_DoughNotFound_ThrowEntityInPizzeriaNotFoundException() {
        Integer doughId = 1;
        when(doughRepository.existsById(doughId)).thenReturn(false);
        EntityInPizzeriaNotFoundException exception = assertThrows(EntityInPizzeriaNotFoundException.class, () -> doughService.delete(doughId));
        assertEquals("Dough not found", exception.getEntityName() + " " + exception.getMessage());
    }

    @Test
    void delete_DoughInUse() {
        Integer doughId = 1;
        when(doughRepository.existsById(doughId)).thenReturn(true);
        when(pizzaRepository.findAllByDoughId(doughId)).thenReturn(Collections.singletonList(new Pizza()));
        DeleteProductException exception = assertThrows(DeleteProductException.class, () -> doughService.delete(doughId));
        assertEquals("Dough already use in pizzas recipe", exception.getMessage());
    }

    @Test
    void getAllForAdmin() {
        List<Dough> doughList = Arrays.asList(TestData.DOUGH_2, TestData.DOUGH_1);
        when(doughRepository.findAll()).thenReturn(doughList);
        List<DoughResponseDto> response = doughService.getAllForAdmin();
        assertFalse(response.isEmpty());
        assertEquals(doughList.size(), response.size());
        assertEquals(doughList.get(0).getTypeDough(), TypeDough.CORNMEAL);
        assertEquals(doughList.get(1).getTypeDough(), TypeDough.CLASSICA);
    }

    @Test
    void getAllForAdmin_EmptyList() {
        List<Dough> emptyList = Collections.emptyList();
        when(doughRepository.findAll()).thenReturn(emptyList);
        List<DoughResponseDto> response = doughService.getAllForAdmin();
        assertTrue(response.isEmpty());
    }

    @Test
    void getAllForClient() {
        List<Dough> doughList = Arrays.asList(TestData.DOUGH_2, TestData.DOUGH_1);
        when(doughRepository.findAll()).thenReturn(doughList);
        List<DoughResponseClientDto> response = doughService.getAllForClient();
        assertFalse(response.isEmpty());
        assertEquals(doughList.size(), response.size());
        assertEquals(doughList.get(0).getTypeDough(), TypeDough.CORNMEAL);
        assertEquals(doughList.get(1).getTypeDough(), TypeDough.CLASSICA);
    }

    @Test
    void getAllForClient_EmptyList() {
        List<Dough> emptyList = Collections.emptyList();
        when(doughRepository.findAll()).thenReturn(emptyList);
        List<DoughResponseClientDto> response = doughService.getAllForClient();
        assertTrue(response.isEmpty());
    }
}