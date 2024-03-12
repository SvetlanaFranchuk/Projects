package org.example.pizzeria.genarateData;

import com.github.javafaker.Faker;
import org.example.pizzeria.entity.product.ingredient.GroupIngredient;
import org.example.pizzeria.entity.product.pizza.Styles;
import org.example.pizzeria.entity.product.pizza.ToppingsFillings;
import org.example.pizzeria.entity.product.pizza.TypeBySize;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GeneratorFaker {
    private static final Faker faker = new Faker();
    private static final Random rnd = new Random();

    public static void main(String[] args) throws FileNotFoundException {
        Set<String[]> ingredients = ingredients();
        File csvOutputFile = new File("ingredient.csv");
        writeTiFile(csvOutputFile, ingredients);

        Set<String[]> pizzas_ingredients = pizzas_ingredients(ingredients);
        File csvOutputFile2 = new File("pizzas_ingredients.csv");
        writeTiFile(csvOutputFile2, pizzas_ingredients);

        Set<String[]> pizzas = pizzas();
        File csvOutputFile3 = new File("pizzas.csv");
        writeTiFile(csvOutputFile3, pizzas);

    }

    public static Set<String[]> pizzas_ingredients(Set<String[]> ingredients){
        Set<String[]> dataLines = new HashSet<>();
        dataLines.add(new String[]{"ingredients_list_id","pizza_set_id"});
        List<String[]> sauces = ingredients.stream()
                .filter(strings -> strings[4].equals("SAUCE"))
                .toList();
        List<String[]> basics = ingredients.stream()
                .filter(strings -> strings[4].equals("BASIC"))
                .toList();
        List<String[]> extras = ingredients.stream()
                .filter(strings -> strings[4].equals("EXTRA"))
                .toList();
        for (int i = 0; i < 30; i++) {
            for (int j = 0; j < 2; j++) {
                String[] str = sauces.get(rnd.nextInt(0, sauces.size()));
                dataLines.add(new String[]{
                        String.valueOf(Integer.parseInt(str[5])+1),
                        String.valueOf(i+1)
                });
            }
            for (int j = 0; j < 3; j++) {
                String[] str = basics.get(rnd.nextInt(0, basics.size()));
                dataLines.add(new String[]{
                        String.valueOf(Integer.parseInt(str[5])+1),
                        String.valueOf(i+1)
                });
            }
            for (int j = 0; j < 5; j++) {
                String[] str = extras.get(rnd.nextInt(0, extras.size()));
                dataLines.add(new String[]{
                        String.valueOf(Integer.parseInt(str[5])+1),
                        String.valueOf(i+1)
                });
            }
        }
        return dataLines;
    }

    public static Set<String[]> pizzas(){
        Set<String[]> dataLines = new HashSet<>();
        dataLines.add(new String[]{"title","description","styles","toppings_fillings","size","is_standard_recipe","dough_id"});
        for (int i = 0; i < 30; i++) {
            String name = faker.funnyName().name();
            dataLines.add(new String[]{
                    name,
                    "description for " + name,
                    Styles.values()[rnd.nextInt(3)].toString(),
                    ToppingsFillings.values()[rnd.nextInt(4)].toString(),
                    TypeBySize.values()[rnd.nextInt(3)].toString(),
                    String.valueOf(rnd.nextBoolean()),
                    String.valueOf(rnd.nextInt(1,7))
            });
        }
        return dataLines;
    }

    public static Set<String[]> ingredients(){
        Set<String[]> dataLines = new HashSet<>();
        dataLines.add(new String[]{"name","weight","nutrition","price","group_ingredient","id"});
        DecimalFormat df = new DecimalFormat("#.####", new DecimalFormatSymbols(Locale.ENGLISH));
        for (int i = 0; i < 300; i++) {
            double randomNumber = rnd.nextDouble() * 2.0;
            String formattedNumber = df.format(randomNumber).replace(',', '.');
            dataLines.add(new String[]{
                    faker.food().ingredient(),
                    String.valueOf(rnd.nextInt(1,200)),
                    String.valueOf(rnd.nextInt(1, 250)),
                    formattedNumber,
                    GroupIngredient.values()[rnd.nextInt(0,3)].toString(),
                    String.valueOf(i)
            });
        }
        return dataLines;
    }

    public static void writeTiFile(File csvOutputFile, Set<String[]> dataLines){
        if (csvOutputFile.exists()) csvOutputFile.delete();

        try (PrintWriter pw = new PrintWriter(csvOutputFile)) {
            dataLines.stream()
                    .map(GeneratorFaker::convertToCSV)
                    .forEach(pw::println);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }}

    public static String convertToCSV(String[] data) {
        return Stream.of(data)
                .map(GeneratorFaker::escapeSpecialCharacters)
                .collect(Collectors.joining(","));
    }

    public static String escapeSpecialCharacters(String data) {
        if (data == null) {
            throw new IllegalArgumentException("Input data cannot be null");
        }
        String escapedData = data.replaceAll("\\R", " ");
        if (data.contains(",") || data.contains("\"") || data.contains("'")) {
            data = data.replace("\"", "\"\"");
            escapedData = "\"" + data + "\"";
        }
        return escapedData;
    }
}