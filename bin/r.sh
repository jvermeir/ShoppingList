#!/bin/bash

./bin/runShop.sh ../ShoppingListData/cookbook_v2.txt ../ShoppingListData/menus/$1.txt ../ShoppingListData/categoryDatabase_v2.csv > $1.txt && open $1.txt

cp $1.txt ~/Dropbox/transfer
