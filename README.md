# ShoppingList

Shopping list is a vehicle to teach myself Scala. And React. 
It also serves a real purpose because it is supposed to generate shopping lists optimized for the super market I visit each week. 
There have been several incarnations of this program: a Oracle Forms version (lost in the mists of time, this is probably a good thing), a Drools version (worked, but was too ambitious because it tried to magically generate a menu as well as a shopping list)
and a couple of Scala versions that tried to support natural language to specify recipes.
None of these previous versions were actually used. While I learned a lot and had a good time doing so, the shopping list problem remained unsolved. Still, I spent lots of time on Saturdays running through 
my favorite  super market knowing full well I'd forget stuff. My kids are now old enough to nag their father about this, so action was necessary.
Ambition can be a dangerous thing if taken in large quantities, so I aimed for an easier goal (and now try to sell this as an advantage since easier goals that deliver working software are OK in true agile spirit).

The road map now looks like this:
- Generate a shopping list based on simple recipes specified as name:value lines in a text string stored in the main function and a menu specified likewise. The domain terms will be in Dutch. (DONE)
- Configure the recipes and menu in text files (DONE)
- Enter the recipes and menu using a GUI
- Generate a menu based on constraints like the season, the amount of time the cook has on a particular day and other fuzzy logic (some of the old ambition shining through here)

Still, the goal to learn always have a strong influence. This will lead to solutions that might be over engineered or rewritten several times just to get it right. 

## Bugs

Entering a recipe that doesn't exist in the cookbook results in a line with 'Dummy' in the output. This should be an error in stead. 

# Build and test

```sbt clean test```

# Package

```sbt assembly``` creates a jar in target/scala-2.12. Copy this into ./bin

# Run

## Commandline utility

```./bin/r.sh <DDMM>```

example

```./bin/r.sh 1301``` takes a menu file named 1301.txt that is stored in ../ShoppingListData/menus, creates a shopping list named 1301.txt in
the root folder of this project and opens this file in your default editor.

## Web service

build as described in `package`

Start rest.WebServer

    java -cp ./target/scala-2.13/shoppinglist-assembly-<version>.jar rest.WebServer
    
e.g.
    
    java -cp ./target/scala-2.13/shoppinglist-assembly-1.7-snapshot.jar rest.WebServer
    
This service reads a cookbook and a category database from `../ShoppingListData/cookbook_v2.txt` and `../ShoppingListData/categoryDatabase_v2.csv`.    
    
on command line:

    curl -v localhost:8080/category/vega
    curl -v localhost:8080/recipe/Lasagne%20%met%20%gehakt
    curl -v localhost:8080/categories
    
## Website

Used Gatsby like this:

    gatsby new shop https://github.com/gatsbyjs/gatsby-starter-hello-world
    
start with

    gatsby develop # localhost only
    gatsby develop --host=0.0.0.0
    
React tutorial

    https://reactjs.org/tutorial/tutorial.html
    
state example:

    https://codepen.io/gaearon/pen/gWWQPY?editors=0010

## UI experiments

done - enter the first day of the planning period using a 
    date picker
done - this changes the start date for the period to the date selected.     
- this loads or creates a menu starting on that day
done - remove days using button 
- add a recipe for a day         
done - allow multiple recipes per day
- add a generate shopping list button
- allow reordering by draging the recipe part
- delete by swiping away

recipe lines show as:

done - day of the week | recipe | delete button
done - day can be changed using dropdown menu
- recipe can be changed by typing in a text field

api:

- find or start menu starting on a date
- add day/recipe line
done remove day/recipe line
- print shopping list
- store menus so we can do some data mining on them

done - show day-month and recipe name in list 
done - use id to update 
done - store date as a datetime so day-month string can be derived.    