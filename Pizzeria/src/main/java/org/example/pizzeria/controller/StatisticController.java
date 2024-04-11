package org.example.pizzeria.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.PastOrPresent;
import org.example.pizzeria.dto.statistic.CountOrdersDto;
import org.example.pizzeria.dto.statistic.IngredientConsumptionDto;
import org.example.pizzeria.dto.statistic.PopularityPizzasDto;
import org.example.pizzeria.dto.statistic.ProfitReportDto;
import org.example.pizzeria.service.statistic.StatisticServiceImpl;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Tag(
        name = "Statistic controller",
        description = "All methods for getting statistic information"
)
@Validated
@RestController
@RequestMapping(path = "stat",
        produces = MediaType.APPLICATION_JSON_VALUE)
@CrossOrigin(origins = "http://localhost:3000")
@PreAuthorize("hasRole('MANAGER')")
public class StatisticController {
    private final StatisticServiceImpl statisticService;

    public StatisticController(StatisticServiceImpl statisticService) {
        this.statisticService = statisticService;
    }

    @Operation(summary = "Getting information about profit")
    @GetMapping("/getProfitInformation")
    public ResponseEntity<ProfitReportDto> getProfitInformation(@Parameter(description = "start date")
                                                                @RequestParam
                                                                @NotNull
                                                                @Past
                                                                @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                                                @Parameter(description = "end date")
                                                                @RequestParam
                                                                @NotNull
                                                                @PastOrPresent
                                                                @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(statisticService.getProfitInformation(startDate, endDate));
    }

    @Operation(summary = "Getting information about ingredient consumption")
    @GetMapping("/getIngredientConsumptionInfo")
    public ResponseEntity<List<IngredientConsumptionDto>> getIngredientConsumptionInfo(@Parameter(description = "start date")
                                                                                       @RequestParam
                                                                                       @NotNull
                                                                                       @Past
                                                                                       @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                                                                       @Parameter(description = "end date")
                                                                                       @RequestParam
                                                                                       @NotNull
                                                                                       @PastOrPresent
                                                                                       @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(statisticService.getIngredientConsumptionInfo(startDate, endDate));
    }

    @Operation(summary = "Getting information about count orders")
    @GetMapping("/getCountOrdersInfo")
    public ResponseEntity<List<CountOrdersDto>> getCountOrdersInfo(@Parameter(description = "start date")
                                                                   @RequestParam
                                                                   @NotNull
                                                                   @Past
                                                                   @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                                                   @Parameter(description = "end date")
                                                                   @RequestParam
                                                                   @NotNull
                                                                   @PastOrPresent
                                                                   @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(statisticService.getCountOrdersInfo(startDate, endDate));
    }

    @Operation(summary = "Getting information about average grade")
    @GetMapping("/getAverageGrade")
    public ResponseEntity<Double> getAverageGrade(@Parameter(description = "start date")
                                                  @RequestParam
                                                  @NotNull
                                                  @Past
                                                  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                                  @Parameter(description = "end date")
                                                  @RequestParam
                                                  @NotNull
                                                  @PastOrPresent
                                                  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(statisticService.getAverageGrade(startDate, endDate));
    }

    @Operation(summary = "Getting information about popularity Pizzas")
    @GetMapping("/getPopularityPizzasInfo")
    public ResponseEntity<List<PopularityPizzasDto>> getPopularityPizzasInfo(@Parameter(description = "start date")
                                                                             @RequestParam
                                                                             @NotNull
                                                                             @Past
                                                                             @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                                                             @Parameter(description = "end date")
                                                                             @RequestParam
                                                                             @NotNull
                                                                             @PastOrPresent
                                                                             @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(statisticService.getPopularityPizzasInfo(startDate, endDate));
    }


}
