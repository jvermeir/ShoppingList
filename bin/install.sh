#!/bin/bash

#
# Create symlinks for vandaag.sh and r.sh scripts.
#

BIN=$(cd "$(dirname "$0")"; pwd -P)

echo ${BIN}

echo linking to ${BIN}/vandaag.sh
rm -f /usr/local/bin/vandaag
ln -s ${BIN}/vandaag.sh /usr/local/bin/vandaag

echo linking to ${BIN}/r.sh
rm -f /usr/local/bin/runshop
ln -s ${BIN}/r.sh /usr/local/bin/runshop

echo linking to ${BIN}/publish.sh
rm -f /usr/local/bin/publish
ln -s ${BIN}/publish.sh /usr/local/bin/publish

echo linking to ${BIN}/toPdf.sh
rm -f /usr/local/bin/toPdf
ln -s ${BIN}/toPdf.sh /usr/local/bin/toPdf
