#!/bin/bash

./bin/runShop.sh ./data/cookbook_v2.txt ./menus/$1.txt > $1.txt && open $1.txt

cp $1.txt ~/Dropbox/transfer
