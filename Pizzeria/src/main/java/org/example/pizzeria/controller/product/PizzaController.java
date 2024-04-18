package org.example.pizzeria.controller.product;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.example.pizzeria.dto.product.pizza.PizzaRequestDto;
import org.example.pizzeria.dto.product.pizza.PizzaResponseDto;
import org.example.pizzeria.entity.product.pizza.Styles;
import org.example.pizzeria.entity.product.pizza.ToppingsFillings;
import org.example.pizzeria.service.product.PizzaServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(
        name = "Product controller",
        description = "All methods for filling out reference books (recipes of pizza) "
)
@Validated
@RestController
@RequestMapping(path = "/pizza",
        produces = MediaType.APPLICATION_JSON_VALUE)
@CrossOrigin(origins = "http://localhost:3000")
public class PizzaController {

    private final PizzaServiceImpl pizzaService;

    @Autowired
    public PizzaController(PizzaServiceImpl pizzaService) {
        this.pizzaService = pizzaService;
    }

    @Operation(summary = "Adding information about new recipe to reference books")
    @PostMapping("/add")
    public ResponseEntity<PizzaResponseDto> add(@RequestBody @Valid PizzaRequestDto newPizza,
                                                @Parameter(description = "user ID")
                                                @RequestParam @Positive @NotNull Long userId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(pizzaService.add(newPizza, userId));
    }

    @Operation(summary = "Updating information about recipe of pizza to reference books")
    @PutMapping("/update/{id}")
    public ResponseEntity<PizzaResponseDto> update(@RequestBody @Valid PizzaRequestDto pizza,
                                                   @Parameter(description = "pizza ID")
                                                   @PathVariable("id") @Positive @NotNull Long id) {
        return ResponseEntity.ok(pizzaService.update(pizza, id));
    }

    @Operation(summary = "Deleting information about recipe of pizza to reference books")
    @DeleteMapping("/deletePizzaRecipe/{id}")
    public ResponseEntity<String> deletePizzaRecipe(@Parameter(description = "pizza ID")
                                                    @PathVariable("id") @Positive @NotNull Long id) {
        pizzaService.deletePizzaRecipe(id);
        return ResponseEntity.ok("Pizza recipe deleted successfully");
    }

    @Operation(summary = "Getting information about all standard recipes of pizzas from the reference books")
    @GetMapping("/getAllPizzaStandardRecipe")
    public List<PizzaResponseDto> getAllPizzaStandardRecipe() {
        return pizzaService.getAllPizzaStandardRecipe();
    }

    @Operation(summary = "Getting information about all standard recipes of pizzas by one of styles from the reference books")
    @GetMapping("/getAllPizzaStandardRecipeByStyles")
    public List<PizzaResponseDto> getAllPizzaStandardRecipeByStyles(@Parameter(description = "Styles: CLASSIC_ITALIAN, AMERICAN, SPECIALITY")
                                                                    @RequestParam Styles styles) {
        return pizzaService.getAllPizzaStandardRecipeByStyles(styles);
    }

    @Operation(summary = "Getting information about all standard recipes of pizzas by one of topping from the reference books")
    @GetMapping("/getAllPizzaStandardRecipeByTopping")
    public List<PizzaResponseDto> getAllPizzaStandardRecipeByTopping(@Parameter(description = "CLASSICA, PAN_PIZZA, " +
            "SICILIAN, NEW_YORK_STYLE, NEAPOLITAN, WHOLE_WHEAT_FLOUR, CORNMEAL")
                                                                     @RequestParam ToppingsFillings toppingsFillings) {
        return pizzaService.getAllPizzaStandardRecipeByTopping(toppingsFillings);
    }

    @Operation(summary = "Getting information about all standard recipes of pizzas by one of topping and styles from the reference books")
    @GetMapping("/getAllPizzaStandardRecipeByToppingByStyles")
    public List<PizzaResponseDto> getAllPizzaStandardRecipeByToppingByStyles(@Parameter(description = "CLASSICA, PAN_PIZZA, " +
            "SICILIAN, NEW_YORK_STYLE, NEAPOLITAN, WHOLE_WHEAT_FLOUR, CORNMEAL")
                                                                             @RequestParam ToppingsFillings toppingsFillings,
                                                                             @Parameter(description = "Styles: CLASSIC_ITALIAN, AMERICAN, SPECIALITY")
                                                                             @RequestParam Styles styles) {
        return pizzaService.getAllPizzaStandardRecipeByToppingByStyles(toppingsFillings, styles);
    }

    @Operation(summary = "Getting information about pizza")
    @GetMapping("/getPizza/{id}")
    public ResponseEntity<PizzaResponseDto> getPizza(@Parameter(description = "pizza ID")
                                                     @PathVariable @Positive @NotNull Long id) {
        return ResponseEntity.ok(pizzaService.getPizza(id));
    }
}
