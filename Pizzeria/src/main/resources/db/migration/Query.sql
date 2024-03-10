INSERT INTO users(login, password, email, birthdate, address, phone_number, is_Blocked, role, count_orders, sum_orders) VALUES ('Admin', 'Qwerty', 'admin@pizza.com', '1990-01-01', '', '', false, 'ADMIN',0,0);
INSERT INTO users(login, password, email, birthdate, address, phone_number, is_Blocked, role, count_orders, sum_orders) VALUES ('Manager', 'Ytrewq', 'manager@pizza.com', '1985-02-10','', '', false, 'MANAGER',0,0);
INSERT INTO users(login, password, email, birthdate, address, phone_number, is_Blocked, role, count_orders, sum_orders) VALUES ('ivanov92', 'ivanov', 'ivanov92@pizza.com', '1991-01-17','Berlin, Rathausstrasse, 15', '+493090000000', false, 'CLIENT',0,0);
INSERT INTO users(login, password, email, birthdate, address, phone_number, is_Blocked, role, count_orders, sum_orders) VALUES ('petrovPP', 'petrov', 'petrovPP@pizza.com', '1995-05-27','Berlin, Rathausstrasse, 45', '+493090000007', false, 'CLIENT',0,0);

INSERT INTO doughs(type_dough, small_weight, small_nutrition, small_price) VALUES ('CLASSICA', 200, 340, 0.25 );
INSERT INTO doughs(type_dough, small_weight, small_nutrition, small_price) VALUES ('PAN_PIZZA', 300, 420, 0.31);
INSERT INTO doughs(type_dough, small_weight, small_nutrition, small_price) VALUES ('SICILIAN', 400, 515, 0.4);
INSERT INTO doughs(type_dough, small_weight, small_nutrition, small_price) VALUES ('NEW_YORK_STYLE', 250, 360, 0.37);
INSERT INTO doughs(type_dough, small_weight, small_nutrition, small_price) VALUES ('NEAPOLITAN', 200, 340, 0.27);
INSERT INTO doughs(type_dough, small_weight, small_nutrition, small_price) VALUES ('WHOLE_WHEAT_FLOUR', 270, 260, 0.37);
INSERT INTO doughs(type_dough, small_weight, small_nutrition, small_price) VALUES ('CORNMEAL', 180, 220, 0.42);

INSERT INTO ingredients(name, weight, nutrition, price, group_ingredient) VALUES ('tomato', 100, 20, 0.08, 'BASIC');
INSERT INTO ingredients(name, weight, nutrition, price, group_ingredient) VALUES ('mozzarella', 100, 280, 0.7, 'BASIC');
INSERT INTO ingredients(name, weight, nutrition, price, group_ingredient) VALUES ('basil', 10, 5, 0.002, 'EXTRA');
INSERT INTO ingredients(name, weight, nutrition, price, group_ingredient) VALUES ('olive oil', 5, 45, 0.12, 'SAUCE');

INSERT INTO pizzas(title, description, styles, toppings_fillings, size, is_standard_recipe, dough_id)
VALUES ('Margaritta','A classic Italian dish that is the perfect combination of simple but delicious ingredients.','CLASSIC_ITALIAN','VEGETABLES','SMALL',true, 1);
INSERT INTO pizzas(title, description, styles, toppings_fillings, size, is_standard_recipe, dough_id)
VALUES ('Margaritta','A classic Italian dish that is the perfect combination of simple but delicious ingredients.','CLASSIC_ITALIAN','VEGETABLES','MEDIUM', true, 1);
INSERT INTO pizzas(title, description, styles, toppings_fillings, size, is_standard_recipe, dough_id)
VALUES ('Margaritta','A classic Italian dish that is the perfect combination of simple but delicious ingredients.','CLASSIC_ITALIAN','VEGETABLES','LARGE', true, 1);

INSERT INTO pizzas_ingredients(ingredients_list_id, pizza_set_id) VALUES (1,1);
INSERT INTO pizzas_ingredients(ingredients_list_id, pizza_set_id) VALUES (2,1);
INSERT INTO pizzas_ingredients(ingredients_list_id, pizza_set_id) VALUES (3,1);
INSERT INTO pizzas_ingredients(ingredients_list_id, pizza_set_id) VALUES (4,1);

INSERT INTO baskets(user_app_id) VALUES (3);
INSERT INTO baskets(user_app_id) VALUES (4);

INSERT INTO baskets_pizzas(basket_id, pizzas_id) VALUES (1,1);

INSERT INTO orders(sum, order_date_time, user_app_id, status_order) VALUES(3.99, '2024-03-09 10:00', 3,'NEW');

INSERT INTO orders_details(delivery_date_time, order_id, type_bonus) VALUES ('2024-03-09', 1, null);
INSERT INTO orders_details_pizzas(order_details_id, pizzas_id) values (1,1);

INSERT INTO favorites(user_app_id) values (2);
INSERT INTO users_favorites(favorites_id, user_app_id) VALUES (1,3);
INSERT INTO pizzas_favorites(favorites_id, pizzas_id) VALUES (1,1);
INSERT INTO reviews(grade, user_app_id, comment) VALUES (null, 3, 'good');
INSERT INTO reviews(grade, user_app_id, comment) VALUES (5, 4, 'ok');
