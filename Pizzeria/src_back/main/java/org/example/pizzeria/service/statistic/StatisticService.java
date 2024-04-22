package org.example.pizzeria.service.statistic;

import org.example.pizzeria.dto.statistic.CountOrdersDto;
import org.example.pizzeria.dto.statistic.IngredientConsumptionDto;
import org.example.pizzeria.dto.statistic.PopularityPizzasDto;
import org.example.pizzeria.dto.statistic.ProfitReportDto;

import java.time.LocalDate;
import java.util.List;

public interface StatisticService {
    ProfitReportDto getProfitInformation(LocalDate startDate, LocalDate endDate);

    List<IngredientConsumptionDto> getIngredientConsumptionInfo(LocalDate startDate, LocalDate endDate);

    List<CountOrdersDto> getCountOrdersInfo(LocalDate startDate, LocalDate endDate);

    Double getAverageGrade(LocalDate startDate, LocalDate endDate);

    List<PopularityPizzasDto> getPopularityPizzasInfo(LocalDate startDate, LocalDate endDate);
}
