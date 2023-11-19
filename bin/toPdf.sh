#!/bin/bash

#
# Print pdf from text file and store in a dropbox folder
#
# Usage: ./toPdf.sh <DDMM>
#        ./toPdf.sh DDMM   this will convert DDMM.txt to DDMM.pdf
#

if test $# -lt 1
then
	echo "Usage: ./toPdf.sh <DDMM>"
	exit 0
fi

enscript $1.txt  --output=$1.ps
ps2pdf $1.ps
mv $1.pdf ~/dropbox/1menu/
rm $1.ps
