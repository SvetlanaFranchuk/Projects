package org.example.pizzeria.exception;

public class ErrorMessage {
    public static final String ENTITY_NOT_FOUND = "not found";
    public static final String INVALID_ID = "Invalid id";
    public static final String USER_ALREADY_EXIST = "User has already exists";
    public static final String USER_BLOCKED = "User can`t leave review";
    public static final String CANT_REVIEW_UPDATED = "User can`t update review";
    public static final String DOUGH_ALREADY_EXIST = "Dough already exists";
    public static final String INGREDIENT_ALREADY_EXIST = "Ingredient already exists";
    public static final String DOUGH_ALREADY_USE_IN_PIZZA = "Dough already use in pizzas recipe";
    public static final String INGREDIENT_ALREADY_USE_IN_PIZZA = "Ingredient already use in pizzas recipe";
    public static final String RECIPE_ALREADY_ORDERED = "This recipe already was in client order";
    public static final String FAVORITES_IS_EMPTY = "Favorites list is empty";
    public static final String NOT_CORRECT_ARGUMENT = "Invalid input parameters";
    public static final String INVALID_STATUS_ORDER_FOR_DELETE = "Order status is not 'NEW', cannot be deleted";
    public static final String INVALID_STATUS_ORDER_FOR_UPDATE = "Order status is not 'NEW', cannot be updated";
    public static final String PIZZA_ALREADY_IN_FAVORITES = "Pizza is already in favorites";


}
