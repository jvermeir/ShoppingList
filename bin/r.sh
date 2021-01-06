#!/bin/bash

if [ -f ${BASH_SOURCE[0]} ];
then
  BIN=$(dirname "$(readlink "${BASH_SOURCE[0]}")")
else
  BIN=$(dirname ${BASH_SOURCE[0]})
fi

DATA=${BIN}/../../ShoppingListData/

${BIN}/runShop.sh ${DATA}/cookbook_v2.txt ${DATA}/menus/$1.txt ${DATA}/categoryDatabase_v2.csv >$1.txt && open $1.txt

cp $1.txt ~/Dropbox/transfer
