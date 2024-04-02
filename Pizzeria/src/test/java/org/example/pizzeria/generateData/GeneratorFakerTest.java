package org.example.pizzeria.generateData;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class GeneratorFakerTest {

    @Test
    void ingredients_ReturnsSetWithSize300() {
        Set<String[]> ingredients = GeneratorFaker.ingredients();
        assertEquals(300, ingredients.size());
    }

    @Test
    void pizzas_ingredients_ReturnsNonEmptySet() {
        Set<String[]> ingredients = GeneratorFaker.ingredients();
        Set<String[]> pizzasIngredients = GeneratorFaker.pizzas_ingredients(ingredients);
        assertFalse(pizzasIngredients.isEmpty());
    }

    @Test
    void pizzas_ReturnsNonEmptySet() {
        Set<String[]> ingredients = GeneratorFaker.ingredients();
        Set<String[]> pizzasIngredients = GeneratorFaker.pizzas_ingredients(ingredients);
        Set<String[]> pizzas = GeneratorFaker.pizzas(pizzasIngredients);
        assertFalse(pizzas.isEmpty());
    }

    @Test
    void writeToFile_CreatesFile() {
        String[] dataLine = new String[]{"1", "Pizza", "Description", "Thin Crust", "Cheese", "Large", "true", "10.0", "100", "1"};
        Set<String[]> dataLines = new HashSet<>();
        dataLines.add(dataLine);
        File csvOutputFile = new File("test.csv");
        GeneratorFaker.writeTiFile(csvOutputFile, dataLines);
        assertTrue(csvOutputFile.exists());
        csvOutputFile.delete();
    }

    @Test
    void convertToCSV_ConvertsDataToArrayCorrectly() {
        String[] data = {"1", "Pizza", "Description", "Thin Crust", "Cheese", "Large", "true", "10.0", "100", "1"};
        String csvString = GeneratorFaker.convertToCSV(data);
        assertEquals("1,Pizza,Description,Thin Crust,Cheese,Large,true,10.0,100,1", csvString);
    }

    @Test
    void escapeSpecialCharacters_EscapesSpecialCharacters() {
        String data = "Pizza, \"thin crust\"";
        String escapedData = GeneratorFaker.escapeSpecialCharacters(data);
        assertEquals("\"Pizza, \"\"thin crust\"\"\"", escapedData);
    }
}