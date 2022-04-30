# Design 

## Data 

A `cookbook` is a list of `recipe`s

A `recipe` has a `name` and a list of `ingredient`s

A `ingredient` has a `name`, a `category` and a `amount` (optional) 

A `category` has a `name` and a `sortOrder`

A `amount` has a `unit` and a `quantity`


A `menu` has a `startDate` and a list of `menuItem`s

A `menuItem` has a `date` and a `recipe` 

A `shoppingList` has a `menu` and a list of `extra`s (optional)

A `extra` has a `ingredient`

## Sample cookbook 
