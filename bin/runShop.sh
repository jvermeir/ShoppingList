#!/bin/bash

# if [ "$1" == "" -o "$2" == "" ]
if test $# -lt 2
then
	echo "Usage: runShop.sh <cookbook> <menu>"
	exit 0
fi
java -cp bin/shop-1.1-jar-with-dependencies.jar shop.ShoppingList $1 $2