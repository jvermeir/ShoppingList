#!/bin/bash

BIN=$(dirname "$(readlink "${BASH_SOURCE[0]}")")
DATA=${BIN}/../../ShoppingListData/
echo ${BIN}
echo ${DATA}

${BIN}/runShop.sh ${DATA}/cookbook_v2.txt ${DATA}/menus/$1.txt ${DATA}/categoryDatabase_v2.csv >$1.txt && open $1.txt

cp $1.txt ~/Dropbox/transfer
