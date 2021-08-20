#!/bin/bash

#
# Print shoppinglist and copy to Dropbox.
#

if [ -f ${BASH_SOURCE[0]} ];
then
  BIN=$(dirname "$(readlink "${BASH_SOURCE[0]}")")
else
  BIN=$(dirname ${BASH_SOURCE[0]})
fi

DATA=${BIN}/../../ShoppingListData/menus

output_folder=$(cd "${DATA}"; pwd -P)

most_recent_menu=$(ls -Art ${output_folder} | tail -n 1)
output=${output_folder}/${most_recent_menu}
echo ${output}
cp ${output} ~/Dropbox/menu
p ${output}
