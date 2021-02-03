#!/bin/bash

#
# Generate a shopping list based on the menu for the specified date.
#
# Usage: ./r.sh <DDMM>
#        ./r.sh 0112   this will use the 0112.txt file in ${BIN}/../../ShoppingListData/menus as input
#

if test $# -lt 1
then
	echo "Usage: ./r.sh <DDMM>"
	exit 0
fi

if [ -f ${BASH_SOURCE[0]} ];
then
  BIN=$(dirname "$(readlink "${BASH_SOURCE[0]}")")
else
  BIN=$(dirname ${BASH_SOURCE[0]})
fi

DATA=${BIN}/../../ShoppingListData/

${BIN}/runShop.sh ${DATA}/cookbook_v2.txt ${DATA}/menus/$1.txt ${DATA}/categoryDatabase_v2.csv >$1.txt && open $1.txt

cp $1.txt ~/Dropbox/transfer
