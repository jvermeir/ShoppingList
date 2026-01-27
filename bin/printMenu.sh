#!/bin/bash

#
# Run the printMenu python script
#
# Usage: ./printMenu.sh <DDMM.txt> [color]
#        (see printMenu.py for details)
#

if [ -f ${BASH_SOURCE[0]} ];
then
  BIN=$(dirname "$(readlink "${BASH_SOURCE[0]}")")
else
  BIN=$(dirname ${BASH_SOURCE[0]})
fi

(cd ${BIN} && uv run printMenu.py "$@")
