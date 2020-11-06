#!/bin/bash

if test $# -lt 3
then
	echo "Usage: runShop.sh <cookbook> <menu> <categoryDatabase>"
	exit 0
fi
java -cp bin/shoppinglist-assembly-1.7.jar shop.Main $1 $2 $3