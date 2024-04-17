package org.example.pizzeria.controller.product;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.example.pizzeria.dto.product.dough.DoughCreateRequestDto;
import org.example.pizzeria.dto.product.dough.DoughResponseClientDto;
import org.example.pizzeria.dto.product.dough.DoughResponseDto;
import org.example.pizzeria.dto.product.dough.DoughUpdateRequestDto;
import org.example.pizzeria.service.product.DoughServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(
        name = "Dough controller",
        description = "All methods for filling out reference books (dough)"
)
@Validated
@RestController
@RequestMapping(path = "/dough",
        produces = MediaType.APPLICATION_JSON_VALUE)
@CrossOrigin(origins = "http://localhost:3000")
public class DoughController {

    private final DoughServiceImpl doughService;

    @Autowired
    public DoughController(DoughServiceImpl doughService) {
        this.doughService = doughService;
    }

    @Operation(summary = "Adding information about dough to reference books",
            description = "Addition is only possible for a unique combination of test type and price")
    @PostMapping("/add")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DoughResponseDto> addDough(@RequestBody @Valid DoughCreateRequestDto newDough) {
        return ResponseEntity.status(HttpStatus.CREATED).body(doughService.add(newDough));
    }

    @Operation(summary = "Update pizza dough parameters: weight and calorie")
    @PatchMapping("/update/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DoughResponseDto> update(@RequestBody @Valid DoughUpdateRequestDto doughUpdateRequestDto,
                                                   @Parameter(description = "id dough")
                                                   @PathVariable("id") @Positive @NotNull Integer id) {
        return ResponseEntity.ok(doughService.update(doughUpdateRequestDto, id));
    }

    @Operation(summary = "Deleting recipe of dough if it no one use")
    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> delete(@Parameter(description = "id dough")
                                         @PathVariable("id") @Positive @NotNull Integer id) {
        doughService.delete(id);
        return ResponseEntity.ok("Dough deleted successfully");
    }

    @Operation(summary = "Show all information about doughs")
    @GetMapping("/getAllForAdmin")
    @PreAuthorize("hasRole('ADMIN')")
    public List<DoughResponseDto> getAllForAdmin() {
        return doughService.getAllForAdmin();
    }

    @Operation(summary = "Show information about doughs for clients")
    @GetMapping("/getAllForClient")
    public List<DoughResponseClientDto> getAllForClient() {
        return doughService.getAllForClient();
    }

}
