package org.example.pizzeria.controller.product;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.example.pizzeria.dto.product.ingredient.IngredientRequestDto;
import org.example.pizzeria.dto.product.ingredient.IngredientResponseClientDto;
import org.example.pizzeria.dto.product.ingredient.IngredientResponseDto;
import org.example.pizzeria.entity.product.ingredient.GroupIngredient;
import org.example.pizzeria.service.product.IngredientServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(
        name = "Ingredient controller",
        description = "All methods for filling out reference books (ingredient)"
)
@Validated
@RestController
@RequestMapping(path = "/ingredient",
        produces = MediaType.APPLICATION_JSON_VALUE)
@CrossOrigin(origins = "http://localhost:3000")
public class IngredientController {

    private final IngredientServiceImpl ingredientService;

    @Autowired
    public IngredientController(IngredientServiceImpl ingredientService) {
        this.ingredientService = ingredientService;
    }

    @Operation(summary = "Adding information about ingredient to reference books")
    @PostMapping("/add")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<IngredientResponseDto> add(@RequestBody @Valid IngredientRequestDto newIngredient) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ingredientService.add(newIngredient));
    }

    @Operation(summary = "Updating information about ingredient to reference books")
    @PutMapping("/update/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<IngredientResponseDto> update(@RequestBody @Valid IngredientRequestDto ingredientRequestDto,
                                                        @Parameter(description = "id ingredient")
                                                        @PathVariable("id") @Positive @NotNull Long id) {
        return ResponseEntity.ok(ingredientService.update(ingredientRequestDto, id));
    }

    @Operation(summary = "Delaying information about ingredient to reference books")
    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> delete(@Parameter(description = "id ingredient")
                                         @PathVariable("id") @Positive @NotNull Long id) {
        ingredientService.delete(id);
        return ResponseEntity.ok("Ingredient deleted successfully");
    }

    @Operation(summary = "Getting information about ingredient")
    @GetMapping("/getAllForAdmin")
    @PreAuthorize("hasRole('ADMIN')")
    public List<IngredientResponseDto> getAllForAdmin() {
        return ingredientService.getAllForAdmin();
    }

    @Operation(summary = "Getting information about ingredient by group ingredient")
    @GetMapping("/getAllByGroup")
    public List<IngredientResponseDto> getAllByGroup(@Parameter(description = "Group ingredient: (BASIC, EXTRA, SAUCE) ")
                                                     @RequestParam GroupIngredient groupIngredient) {
        return ingredientService.getAllByGroup(groupIngredient);
    }

    @Operation(summary = "Getting information about ingredient into the pizza")
    @GetMapping("/{idPizza}/ingredients")
    public List<IngredientResponseClientDto> getAllForPizza(@Parameter(description = "pizzas id")
                                                            @PathVariable("idPizza") @Positive @NotNull Long idPizza) {
        return ingredientService.getAllForPizza(idPizza);
    }

}
