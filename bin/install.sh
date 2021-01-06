#!/bin/bash

if [ -f ${BASH_SOURCE[0]} ];
then
  BIN=$(dirname "$(readlink "${BASH_SOURCE[0]}")")
else
  BIN=$(dirname ${BASH_SOURCE[0]})
fi

rm -f /usr/local/bin/vandaag
ln -s ${BIN}/vandaag.sh /usr/local/bin/vandaag
rm -f /usr/local/bin/runshop
ln -s ${BIN}/r.sh /usr/local/bin/runshop
