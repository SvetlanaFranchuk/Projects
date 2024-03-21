package org.example.pizzeria.service.product;

import org.example.pizzeria.dto.product.dough.DoughRequestDto;
import org.example.pizzeria.dto.product.dough.DoughResponseClientDto;
import org.example.pizzeria.dto.product.dough.DoughResponseDto;
import org.example.pizzeria.entity.product.ingredient.Dough;
import org.example.pizzeria.entity.product.ingredient.TypeDough;
import org.example.pizzeria.entity.product.pizza.Pizza;
import org.example.pizzeria.exception.InvalidIDException;
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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceImpl_Test_Dough {
    private static final DoughRequestDto DOUGH_REQUEST_DTO = new DoughRequestDto(TypeDough.CLASSICA, 100, 120, 0.23);
    private static final DoughRequestDto DOUGH_REQUEST_DTO_NEW = new DoughRequestDto(TypeDough.CORNMEAL, 120, 140, 0.25);
    private static final DoughResponseDto DOUGH_RESPONSE_DTO_NEW = new DoughResponseDto(TypeDough.CORNMEAL, 120, 140, 0.25);
    private static final DoughResponseDto DOUGH_RESPONSE_DTO = new DoughResponseDto(TypeDough.CLASSICA, 100, 120, 0.23);
    @Mock
    private DoughRepository doughRepository;
    @Mock
    private PizzaRepository pizzaRepository;
@Mock
private DoughMapper doughMapper;
    @InjectMocks
    private ProductServiceImpl productService;

    @BeforeEach
    void setUp() {
        Mockito.reset(doughRepository);
        Mockito.reset(pizzaRepository);
    }

    @Test
    void addDough() {
        Dough dough = new Dough(1,TypeDough.CLASSICA, 100, 120, 0.23);
        when(doughMapper.toDough(DOUGH_REQUEST_DTO.typeDough(), DOUGH_REQUEST_DTO.smallWeight(),
                DOUGH_REQUEST_DTO.smallNutrition(), DOUGH_REQUEST_DTO.smallPrice())).thenReturn(dough);
        when(doughRepository.save(dough)).thenReturn(dough);
        when(doughMapper.toDoughResponseDto(dough)).thenReturn(DOUGH_RESPONSE_DTO);

        DoughResponseDto result = productService.addDough(DOUGH_REQUEST_DTO);
        assertEquals(DOUGH_RESPONSE_DTO, result);
    }

    @Test
    void addDough_DuplicateDoughTypeAndPrice() {
        Dough existingDough = new Dough(1, TypeDough.CLASSICA, 120, 140, 0.23);
        when(doughRepository.findAllByTypeDoughAndSmallPrice(TypeDough.CLASSICA, 0.23))
                .thenReturn(Collections.singletonList(existingDough));
        assertThrows(DoughCreateException.class, () -> productService.addDough(DOUGH_REQUEST_DTO));
    }

    @Test
    void updateDough() {
        Dough existingDough = new Dough(1, TypeDough.CORNMEAL, 130, 140, 0.23);
        Dough newDough = new Dough(1, TypeDough.CORNMEAL, 120, 140, 0.25);
        when(doughRepository.findById(1)).thenReturn(Optional.of(existingDough));
        when(doughRepository.save(newDough)).thenReturn(newDough);
        when(doughMapper.toDoughResponseDto(newDough)).thenReturn(DOUGH_RESPONSE_DTO_NEW);
        DoughResponseDto responseDto = productService.updateDough(DOUGH_REQUEST_DTO_NEW, 1);
        assertEquals(DOUGH_RESPONSE_DTO_NEW, responseDto);
    }

    @Test
    void updateDough_InvalidID() {
        DoughRequestDto updatedDough = new DoughRequestDto(TypeDough.CORNMEAL, 120, 140, 0.25);
        assertThrows(InvalidIDException.class, () -> productService.updateDough(updatedDough, null));
    }

    @Test
    void deleteDough() {
        Dough dough = new Dough(1, TypeDough.CORNMEAL, 130, 140, 0.23);
        when(doughRepository.findById(1)).thenReturn(Optional.of(dough));
        when(pizzaRepository.findAllByDoughIs(dough)).thenReturn(Collections.emptyList());
        productService.deleteDough(1);
    }
    @Test
    void deleteDough_DoughNotFound() {
          when(doughRepository.findById(1)).thenReturn(Optional.empty());
          assertThrows(InvalidIDException.class, () -> productService.deleteDough(1));
    }

    @Test
    void deleteDough_DoughInUse() {
        Dough dough = new Dough(1, TypeDough.CORNMEAL, 130, 140, 0.23);
        when(doughRepository.findById(1)).thenReturn(Optional.of(dough));
        when(pizzaRepository.findAllByDoughIs(dough)).thenReturn(Collections.singletonList(new Pizza()));
        assertThrows(DeleteProductException.class, () -> productService.deleteDough(1));
    }
    @Test
    void getAllDoughForAdmin() {
        List<Dough> doughList = Arrays.asList(
                new Dough(1, TypeDough.CORNMEAL, 130, 140, 0.23),
                new Dough(2, TypeDough.CLASSICA, 100, 120, 0.21)
        );
        when(doughRepository.findAll()).thenReturn(doughList);
        List<DoughResponseDto> response = productService.getAllDoughForAdmin();
        assertFalse(response.isEmpty());
        assertEquals(doughList.size(), response.size());
        assertEquals(doughList.get(0).getTypeDough(), TypeDough.CORNMEAL);
        assertEquals(doughList.get(1).getTypeDough(), TypeDough.CLASSICA);
    }

    @Test
    void getAllDoughForAdmin_EmptyList() {
        List<Dough> emptyList = Collections.emptyList();
        when(doughRepository.findAll()).thenReturn(emptyList);
        List<DoughResponseDto> response = productService.getAllDoughForAdmin();
        assertTrue(response.isEmpty());
    }

    @Test
    void getAllDoughForClient() {
        List<Dough> doughList = Arrays.asList(
                new Dough(1, TypeDough.CORNMEAL, 130, 140, 0.23),
                new Dough(2, TypeDough.CLASSICA, 100, 120, 0.21)
        );
        when(doughRepository.findAll()).thenReturn(doughList);
        List<DoughResponseClientDto> response = productService.getAllDoughForClient();
        assertFalse(response.isEmpty());
        assertEquals(doughList.size(), response.size());
        assertEquals(doughList.get(0).getTypeDough(), TypeDough.CORNMEAL);
        assertEquals(doughList.get(1).getTypeDough(), TypeDough.CLASSICA);
    }

    @Test
    void getAllDoughForClient_EmptyList() {
        List<Dough> emptyList = Collections.emptyList();
        when(doughRepository.findAll()).thenReturn(emptyList);
        List<DoughResponseClientDto> response = productService.getAllDoughForClient();
        assertTrue(response.isEmpty());
    }
}