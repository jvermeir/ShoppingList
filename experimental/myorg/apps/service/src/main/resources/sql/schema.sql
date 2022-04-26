CREATE TABLE IF NOT EXISTS messages (
    id                     VARCHAR(60)  DEFAULT RANDOM_UUID() PRIMARY KEY,
    text                   VARCHAR      NOT NULL
    );

CREATE TABLE IF NOT EXISTS menus (
  id                     VARCHAR(60)  DEFAULT RANDOM_UUID() PRIMARY KEY,
  firstDay                   DATE      NOT NULL
  );

CREATE TABLE IF NOT EXISTS menuItem (
  id                     VARCHAR(60)  DEFAULT RANDOM_UUID() PRIMARY KEY,
  day DATE NOT NULL,
  recipe VARCHAR(60) NOT NULL,
  menu  VARCHAR(60) NOT NULL
)
