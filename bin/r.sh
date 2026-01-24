#!/bin/bash

#
# Generate a shopping list based on the menu for the specified date.
#
# Usage: ./r.sh <DDMM> [MENU_FOLDER]
#        ./r.sh 0112   this will use the 0112.txt file in ${BIN}/../../ShoppingListData/menus as input
#
# MENU_FOLDER is an optional parameter specifying the folder where the output file will be saved.
# If not provided, it defaults to ~/Library/CloudStorage/Dropbox-Personal/1menu.
# This is useful for syncing the output file with cloud services (e.g., Dropbox) for easy access on other devices.
#
# The output file is copied in MENU_FOLDER and opened with the default text editor.
#

if test $# -lt 1
then
	echo "Usage: ./r.sh <DDMM> [MENU_FOLDER]"
	exit 0
fi

if [ -f ${BASH_SOURCE[0]} ];
then
  BIN=$(dirname "$(readlink "${BASH_SOURCE[0]}")")
else
  BIN=$(dirname ${BASH_SOURCE[0]})
fi

DATA=${BIN}/../../ShoppingListData/
MENU_FOLDER="${2:-${HOME}/dropbox/1menu}"
OUTPUT_FILE=$MENU_FOLDER/$1.txt

echo "data: $DATA"
echo "bin: $BIN"
echo "output: $OUTPUT_FILE"
echo "menu folder: $MENU_FOLDER"


${BIN}/runShop.sh ${DATA}/cookbook_v2.txt ${DATA}/menus/$1.txt ${DATA}/categoryDatabase_v2.txt > ${OUTPUT_FILE}

open $OUTPUT_FILE
