CREATE TABLE IF NOT EXISTS "menus"
(
  "id"        VARCHAR(60) DEFAULT RANDOM_UUID() PRIMARY KEY,
  "first_day" DATE
);

CREATE TABLE IF NOT EXISTS "shopping_lists"
(
  "id"        VARCHAR(60) DEFAULT RANDOM_UUID() PRIMARY KEY,
  "first_day" DATE
);

CREATE TABLE IF NOT EXISTS "menu_items"
(
  "id"        VARCHAR(60) DEFAULT RANDOM_UUID() PRIMARY KEY,
  "the_day"   DATE        NOT NULL,
  "recipe_id" VARCHAR(60) NOT NULL,
  "menu_id"   VARCHAR(60) NOT NULL
);

CREATE TABLE IF NOT EXISTS "categories"
(
  "id"         VARCHAR(60) DEFAULT RANDOM_UUID() PRIMARY KEY,
  "name"       VARCHAR    NOT NULL UNIQUE,
  "shop_order" NUMBER(10) NOT NULL
);

CREATE TABLE IF NOT EXISTS "shopping_list_categories"
(
  "id"               VARCHAR(60) DEFAULT RANDOM_UUID() PRIMARY KEY,
  "category_id"      VARCHAR(60) NOT NULL,
  "shopping_list_id" VARCHAR(60) NOT NULL,
  "name"             VARCHAR     NOT NULL,
  "shop_order"       NUMBER(10)  NOT NULL
);

CREATE TABLE IF NOT EXISTS "ingredients"
(
  "id"          VARCHAR(60)          DEFAULT RANDOM_UUID() PRIMARY KEY,
  "name"        VARCHAR     NOT NULL UNIQUE,
  "category_id" VARCHAR(60),
  "unit"        VARCHAR(60) NOT NULL DEFAULT 'kg'
);

CREATE TABLE IF NOT EXISTS "shopping_list_ingredients"
(
  "id"                        VARCHAR(60)          DEFAULT RANDOM_UUID() PRIMARY KEY,
  "ingredient_id"             VARCHAR(60) NOT NULL,
  "name"                      VARCHAR     NOT NULL,
  "shopping_list_category_id" VARCHAR(60) NOT NULL,
  "amount"                    NUMBER(10)  NOT NULL DEFAULT 0,
  "unit"                      VARCHAR(60) NOT NULL DEFAULT 'kg'
);

CREATE TABLE IF NOT EXISTS "recipes"
(
  "id"       VARCHAR(60) DEFAULT RANDOM_UUID() PRIMARY KEY,
  "name"     VARCHAR NOT NULL,
  "favorite" BOOLEAN NOT NULL
);

CREATE TABLE IF NOT EXISTS "recipe_ingredients"
(
  "id"            VARCHAR(60)          DEFAULT RANDOM_UUID() PRIMARY KEY,
  "recipe_id"     VARCHAR(60) NOT NULL,
  "ingredient_id" VARCHAR(60) NOT NULL,
  "amount"        NUMBER(10)  NOT NULL DEFAULT 0,
  "unit"          VARCHAR(60) NOT NULL DEFAULT 'kg'
);
