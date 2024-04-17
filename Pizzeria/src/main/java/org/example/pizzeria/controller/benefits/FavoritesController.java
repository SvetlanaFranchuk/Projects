package org.example.pizzeria.controller.benefits;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.example.pizzeria.dto.benefits.FavoritesResponseDto;
import org.example.pizzeria.dto.product.pizza.PizzaResponseDto;
import org.example.pizzeria.service.benefits.FavoritesServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(
        name = "Favorites controller",
        description = "All methods for adding recipe to users favorite"
)
@Validated
@RestController
@RequestMapping(path = "/favorites",
        produces = MediaType.APPLICATION_JSON_VALUE)
@CrossOrigin(origins = "http://localhost:3000")
public class FavoritesController {

    private final FavoritesServiceImpl favoritesService;

    @Autowired
    public FavoritesController(FavoritesServiceImpl favoritesService) {
        this.favoritesService = favoritesService;
    }

    @Operation(summary = "Adding pizza to users favorites")
    @PostMapping("/addPizzaToUserFavorite")
    public ResponseEntity<FavoritesResponseDto> addPizzaToUserFavorite(@Parameter(description = "user ID")
                                                                       @RequestParam @Positive @NotNull Long userId,
                                                                       @Parameter(description = "pizza ID")
                                                                       @RequestParam @Positive @NotNull Long pizzaId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(favoritesService.addPizzaToUserFavorite(userId, pizzaId));
    }

    @Operation(summary = "Deleting pizza from users favorites")
    @DeleteMapping("/deletePizzaFromUserFavorite")
    public ResponseEntity<String> deletePizzaFromUserFavorite(@Parameter(description = "user ID")
                                                              @RequestParam @Positive @NotNull Long userId,
                                                              @Parameter(description = "pizza ID")
                                                              @RequestParam @Positive @NotNull Long pizzaId) {
        favoritesService.deletePizzaFromUserFavorite(pizzaId, userId);
        return ResponseEntity.ok("Pizza deleted from user's favorites successfully");
    }

    @Operation(summary = "Getting all favorites pizza of users")
    @GetMapping("/getAllFavoritePizzaByUser/{userId}")
    public ResponseEntity<List<PizzaResponseDto>> getAllFavoritePizzaByUser(@Parameter(description = "user ID")
                                                                            @PathVariable @Positive @NotNull Long userId) {
        return ResponseEntity.ok(favoritesService.getAllFavoritePizzaByUser(userId));
    }
}
