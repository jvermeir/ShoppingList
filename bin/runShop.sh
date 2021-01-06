#!/bin/bash

if [ -L ${BASH_SOURCE[0]} ];
then
  BIN=$(dirname "$(readlink "${BASH_SOURCE[0]}")")
else
  BIN=$(dirname ${BASH_SOURCE[0]})
fi

if test $# -lt 3
then
	echo "Usage: runShop.sh <cookbook> <menu> <categoryDatabase>"
	exit 0
fi

java -cp ${BIN}/shoppinglist-assembly-1.7.jar shop.Main $1 $2 $3