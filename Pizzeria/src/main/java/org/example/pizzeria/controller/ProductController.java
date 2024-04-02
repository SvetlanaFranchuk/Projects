package org.example.pizzeria.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
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
import org.example.pizzeria.entity.product.ingredient.GroupIngredient;
import org.example.pizzeria.entity.product.pizza.Styles;
import org.example.pizzeria.entity.product.pizza.ToppingsFillings;
import org.example.pizzeria.exception.EntityInPizzeriaNotFoundException;
import org.example.pizzeria.service.product.ProductServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(
        name = "Product controller",
        description = "All methods for filling out reference books, recipes of pizza and also for adding recipe to users favorite"
)
@Validated
@RestController
@RequestMapping(path = "product",
        produces = MediaType.APPLICATION_JSON_VALUE)
@CrossOrigin(origins = "http://localhost:3000")
public class ProductController {

    private final ProductServiceImpl productService;

    @Autowired
    public ProductController(ProductServiceImpl productService) {
        this.productService = productService;
    }

    @Operation(summary = "Adding information about dough to reference books",
            description = "Addition is only possible for a unique combination of test type and price")
    @PostMapping("/addDough")
    public ResponseEntity<DoughResponseDto> addDough(@RequestBody @Valid DoughCreateRequestDto newDough) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.addDough(newDough));
    }

    @Operation(summary = "Update pizza dough parameters: weight and calorie")
    @PatchMapping("/updateDough/{id}")
    public ResponseEntity<DoughResponseDto> updateDough(@RequestBody @Valid DoughUpdateRequestDto doughUpdateRequestDto,
                                                        @Parameter(description = "id dough")
                                                        @PathVariable("id") @Min(0) Integer id) {
        DoughResponseDto updatedDough = productService.updateDough(doughUpdateRequestDto, id);
        if (updatedDough != null) {
            return ResponseEntity.ok(updatedDough);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Deleting recipe of dough if it no one use")
    @DeleteMapping("/deleteDough/{id}")
    public ResponseEntity<String> deleteDough(@Parameter(description = "id dough")
                                              @PathVariable("id") @Min(0) Integer id) {
        productService.deleteDough(id);
        return ResponseEntity.ok("Dough deleted successfully");
    }

    @Operation(summary = "Show all information about doughs")
    @GetMapping("/getAllDoughForAdmin")
    public List<DoughResponseDto> getAllDoughForAdmin() {
        return productService.getAllDoughForAdmin();
    }

    @Operation(summary = "Show information about doughs for clients")
    @GetMapping("/getAllDoughForClient")
    public List<DoughResponseClientDto> getAllDoughForClient() {
        return productService.getAllDoughForClient();
    }

    @Operation(summary = "Adding information about ingredient to reference books")
    @PostMapping("/addIngredient")
    public ResponseEntity<IngredientResponseDto> addIngredient(@RequestBody @Valid IngredientRequestDto newIngredient) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.addIngredient(newIngredient));
    }

    @Operation(summary = "Updating information about ingredient to reference books")
    @PutMapping("/updateIngredient/{id}")
    public ResponseEntity<IngredientResponseDto> updateIngredient(@RequestBody @Valid IngredientRequestDto ingredientRequestDto,
                                                                  @Parameter(description = "id ingredient")
                                                                  @PathVariable("id") @Min(0) Long id) {
        IngredientResponseDto IngredientResponseDto = productService.updateIngredient(ingredientRequestDto, id);
        if (IngredientResponseDto != null) {
            return ResponseEntity.ok(IngredientResponseDto);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Delaying information about ingredient to reference books")
    @DeleteMapping("/deleteIngredient/{id}")
    public ResponseEntity<String> deleteIngredient(@Parameter(description = "id ingredient")
                                                   @PathVariable("id") @Min(0) Long id) {
        productService.deleteIngredient(id);
        return ResponseEntity.ok("Ingredient deleted successfully");
    }

    @Operation(summary = "Getting information about ingredient")
    @GetMapping("/getAllIngredientForAdmin")
    public List<IngredientResponseDto> getAllIngredientForAdmin() {
        return productService.getAllIngredientForAdmin();
    }

    @Operation(summary = "Getting information about ingredient by group ingredient")
    @GetMapping("/getAllIngredientByGroup")
    public List<IngredientResponseDto> getAllIngredientByGroup(@Parameter(description = "Group ingredient: (BASIC, EXTRA, SAUCE) ")
                                                               @RequestParam GroupIngredient groupIngredient) {
        return productService.getAllIngredientByGroup(groupIngredient);
    }

    @Operation(summary = "Getting information about ingredient into the pizza")
    @GetMapping("/{idPizza}/ingredients")
    public List<IngredientResponseClientDto> getAllIngredientsForPizza(@Parameter(description = "pizzas id")
                                                                       @PathVariable("idPizza") @Min(0) Long idPizza) {
        return productService.getAllIngredientForPizza(idPizza);
    }

    @Operation(summary = "Adding information about new recipe to reference books")
    @PostMapping("/addPizza")
    public ResponseEntity<PizzaResponseDto> addPizza(@RequestBody @Valid PizzaRequestDto newPizza,
                                                     @Parameter(description = "user ID")
                                                     @RequestParam @Min(0) Long userId) {
        PizzaResponseDto addedPizza = productService.addPizza(newPizza, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(addedPizza);
    }

    @Operation(summary = "Updating information about recipe of pizza to reference books")
    @PutMapping("/updatePizza/{id}")
    public ResponseEntity<PizzaResponseDto> updatePizza(@RequestBody @Valid PizzaRequestDto pizza,
                                                        @Parameter(description = "pizza ID")
                                                        @PathVariable("id") @Min(0) Long id) {
        PizzaResponseDto updatedPizza = productService.updatePizza(pizza, id);
        if (updatedPizza != null) {
            return ResponseEntity.ok(updatedPizza);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Deleting information about recipe of pizza to reference books")
    @DeleteMapping("/deletePizzaRecipe/{id}")
    public ResponseEntity<String> deletePizzaRecipe(@Parameter(description = "pizza ID")
                                                    @PathVariable("id") @Min(0) Long id) {
        productService.deletePizzaRecipe(id);
        return ResponseEntity.ok("Pizza recipe deleted successfully");
    }

    @Operation(summary = "Getting information about all standard recipes of pizzas from the reference books")
    @GetMapping("/getAllPizzaStandardRecipe")
    public List<PizzaResponseDto> getAllPizzaStandardRecipe() {
        return productService.getAllPizzaStandardRecipe();
    }

    @Operation(summary = "Getting information about all standard recipes of pizzas by one of styles from the reference books")
    @GetMapping("/getAllPizzaStandardRecipeByStyles")
    public List<PizzaResponseDto> getAllPizzaStandardRecipeByStyles(@Parameter(description = "Styles: CLASSIC_ITALIAN, AMERICAN, SPECIALITY")
                                                                    @RequestParam Styles styles) {
        return productService.getAllPizzaStandardRecipeByStyles(styles);
    }

    @Operation(summary = "Getting information about all standard recipes of pizzas by one of topping from the reference books")
    @GetMapping("/getAllPizzaStandardRecipeByTopping")
    public List<PizzaResponseDto> getAllPizzaStandardRecipeByTopping(@Parameter(description = "CLASSICA, PAN_PIZZA, " +
            "SICILIAN, NEW_YORK_STYLE, NEAPOLITAN, WHOLE_WHEAT_FLOUR, CORNMEAL")
                                                                     @RequestParam ToppingsFillings toppingsFillings) {
        return productService.getAllPizzaStandardRecipeByTopping(toppingsFillings);
    }

    @Operation(summary = "Getting information about all standard recipes of pizzas by one of topping and styles from the reference books")
    @GetMapping("/getAllPizzaStandardRecipeByToppingByStyles")
    public List<PizzaResponseDto> getAllPizzaStandardRecipeByToppingByStyles(@Parameter(description = "CLASSICA, PAN_PIZZA, " +
            "SICILIAN, NEW_YORK_STYLE, NEAPOLITAN, WHOLE_WHEAT_FLOUR, CORNMEAL")
                                                                             @RequestParam ToppingsFillings toppingsFillings,
                                                                             @Parameter(description = "Styles: CLASSIC_ITALIAN, AMERICAN, SPECIALITY")
                                                                             @RequestParam Styles styles) {
        return productService.getAllPizzaStandardRecipeByToppingByStyles(toppingsFillings, styles);
    }

    @Operation(summary = "Adding pizza to users favorites")
    @PostMapping("/addPizzaToUserFavorite")
    public ResponseEntity<FavoritesResponseDto> addPizzaToUserFavorite(@Parameter(description = "user ID")
                                                                       @RequestParam @Min(0) Long userId,
                                                                       @Parameter(description = "pizza ID")
                                                                       @RequestParam @Valid Long pizzaId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.addPizzaToUserFavorite(userId, pizzaId));
    }

    @Operation(summary = "Deleting pizza from users favorites")
    @DeleteMapping("/deletePizzaFromUserFavorite")
    public ResponseEntity<String> deletePizzaFromUserFavorite(@Parameter(description = "user ID")
                                                              @RequestParam @Min(0) Long userId,
                                                              @Parameter(description = "pizza ID")
                                                              @RequestParam @Valid Long pizzaId) {
        productService.deletePizzaFromUserFavorite(pizzaId, userId);
        return ResponseEntity.ok("Pizza deleted from user's favorites successfully");
    }

    @Operation(summary = "Getting all favorites pizza of users")
    @GetMapping("/getAllFavoritePizzaByUser/{userId}")
    public ResponseEntity<List<PizzaResponseDto>> getAllFavoritePizzaByUser(@Parameter(description = "user ID")
                                                                            @RequestParam @Min(0) Long userId) {
        return ResponseEntity.ok(productService.getAllFavoritePizzaByUser(userId));
    }

    @Operation(summary = "Getting information about pizza")
    @GetMapping("/getPizza/{id}")
    public ResponseEntity<PizzaResponseDto> getPizza(@PathVariable @Positive Long id) {
        try {
            PizzaResponseDto pizzaResponseDto = productService.getPizza(id);
            return ResponseEntity.ok(pizzaResponseDto);
        } catch (EntityInPizzeriaNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
