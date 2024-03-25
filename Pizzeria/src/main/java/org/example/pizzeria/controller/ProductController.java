package org.example.pizzeria.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
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
import org.example.pizzeria.entity.product.pizza.Pizza;
import org.example.pizzeria.entity.product.pizza.Styles;
import org.example.pizzeria.entity.product.pizza.ToppingsFillings;
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

    @Operation(summary = "Adding information about dough to reference books. Addition is only possible for a unique combination of test type and price")
    @PostMapping("/addDough")
    public ResponseEntity<DoughResponseDto> addDough(@RequestBody @Valid DoughCreateRequestDto newDough) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.addDough(newDough));
    }

    @Operation(summary = "Update pizza dough parameters: weight and calorie")
    @PutMapping("/updateDough/{id}")
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

    @PostMapping("/addIngredient")
    public ResponseEntity<IngredientResponseDto> addIngredient(@RequestBody @Valid IngredientRequestDto newIngredient) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.addIngredient(newIngredient));
    }//TODO не генеригует ИД???

    @PutMapping("/updateIngredient/{id}")
    public ResponseEntity<IngredientResponseDto> updateDough(@RequestBody @Valid IngredientRequestDto ingredientRequestDto,
                                                             @PathVariable("id") @Min(0) Long id) {
        IngredientResponseDto IngredientResponseDto = productService.updateIngredient(ingredientRequestDto, id);
        if (IngredientResponseDto != null) {
            return ResponseEntity.ok(IngredientResponseDto);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/deleteIngredient/{id}")
    public ResponseEntity<String> deleteIngredient(@PathVariable("id") @Min(0) Long id) {
        productService.deleteIngredient(id);
        return ResponseEntity.ok("Ingredient deleted successfully");
    } //TODO Ingredient already use in pizzas recipe ?????

    @GetMapping("/getAllIngredientForAdmin")
    public List<IngredientResponseDto> getAllIngredientForAdmin() {
        return productService.getAllIngredientForAdmin();
    }

    @GetMapping("/getAllIngredientByGroup/{groupIngredient}")
    public List<IngredientResponseClientDto> getAllIngredientByGroup(@RequestParam GroupIngredient groupIngredient) {
        return productService.getAllIngredientByGroup(groupIngredient);
    }

    @GetMapping("/{idPizza}/ingredients")
    public List<IngredientResponseClientDto> getAllIngredientsForPizza(@PathVariable("id") @Min(0) Long idPizza) {
        return productService.getAllIngredientForPizza(idPizza);
    }

    @PostMapping("/addPizza/{userId}")
    public ResponseEntity<PizzaResponseDto> addPizza(@RequestBody @Valid PizzaRequestDto newPizza,
                                                     @PathVariable("id") @Min(0) Long userId) {
        PizzaResponseDto addedPizza = productService.addPizza(newPizza, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(addedPizza);
    }

    @PutMapping("/updatePizza/{id}")
    public ResponseEntity<PizzaResponseDto> updatePizza(@RequestBody @Valid PizzaRequestDto pizza,
                                                        @PathVariable("id") @Min(0) Long id) {
        PizzaResponseDto updatedPizza = productService.updatePizza(pizza, id);
        if (updatedPizza != null) {
            return ResponseEntity.ok(updatedPizza);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/deletePizzaRecipe/{id}")
    public ResponseEntity<String> deletePizzaRecipe(@PathVariable("id") @Min(0) Long id) {
        productService.deletePizzaRecipe(id);
        return ResponseEntity.ok("Pizza recipe deleted successfully");
    }

    @GetMapping("/getAllPizzaStandardRecipe")
    public List<PizzaResponseDto> getAllPizzaStandardRecipe() {
        return productService.getAllPizzaStandardRecipe();
    }

    @GetMapping("/getAllPizzaStandardRecipeByStyles/{styles}")
    public List<PizzaResponseDto> getAllPizzaStandardRecipeByStyles(@RequestParam Styles styles) {
        return productService.getAllPizzaStandardRecipeByStyles(styles);
    }

    @GetMapping("/getAllPizzaStandardRecipeByTopping/{topping}")
    public List<PizzaResponseDto> getAllPizzaStandardRecipeByTopping(@RequestParam ToppingsFillings toppingsFillings) {
        return productService.getAllPizzaStandardRecipeByTopping(toppingsFillings);
    }

    @GetMapping("/getAllPizzaStandardRecipeByToppingByStyles/{topping}/{styles}")
    public List<PizzaResponseDto> getAllPizzaStandardRecipeByToppingByStyles(@RequestParam ToppingsFillings toppingsFillings,
                                                                             @RequestParam Styles styles) {
        return productService.getAllPizzaStandardRecipeByToppingByStyles(toppingsFillings, styles);
    }

    @PostMapping("/addPizzaToUserFavorite/{userId}")
    public ResponseEntity<FavoritesResponseDto> addPizzaToUserFavorite(@PathVariable("id") @Min(0) Long userId,
                                                                       @RequestBody @Valid Pizza pizza) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.addPizzaToUserFavorite(userId, pizza));
    }

    @DeleteMapping("/deletePizzaFromUserFavorite/{userId}/{pizzaId}")
    public ResponseEntity<String> deletePizzaFromUserFavorite(@PathVariable Long userId,
                                                              @PathVariable Long pizzaId) {
        productService.deletePizzaFromUserFavorite(pizzaId, userId);
        return ResponseEntity.ok("Pizza deleted from user's favorites successfully");
    }

    @GetMapping("/getAllFavoritePizzaByUser/{userId}")
    public ResponseEntity<List<PizzaResponseDto>> getAllFavoritePizzaByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(productService.getAllFavoritePizzaByUser(userId));
    }
}
