#!/bin/bash

#
# Generate empty menu file starting on today or as many days in the future as specified by the optional parameter.
#
# Usage: vandaag.sh
#        vandaag.sh 2  will create a men for the day after tomorrow
#

export LC_ALL=nl_NL

if [ -z $1 ];
then
  start_date=$(date -v +0d +%d%m%Y)
else
  start_date=$(date -v +${1}d +%d%m%Y)
fi

if [ -f ${BASH_SOURCE[0]} ];
then
  BIN=$(dirname "$(readlink "${BASH_SOURCE[0]}")")
else
  BIN=$(dirname ${BASH_SOURCE[0]})
fi

today=$(date -j -f %d%m%Y ${start_date} +%d%m%Y)
file_name=$(date -j -f %d%m%Y ${start_date} +%d%m.txt)
week_day=$(date -j -f %d%m%Y ${start_date} +%A)
tmp_output_folder=${BIN}/../../ShoppingListData/menus
output_folder=$(cd "${tmp_output_folder}"; pwd -P)

mkdir -p ${output_folder}
output_file=${output_folder}/${file_name}
rm -rf ${output_file}
touch ${output_file}
echo "${week_day} valt op:${today}" > ${output_file}
for i in {0..6}; do
  the_day=$(date -j -v +${i}d -f %d%m%Y ${today} +%A)
  echo ${the_day}:- >> ${output_file}
done

echo "" >> ${output_file}
echo "extra" >> ${output_file}
echo "boter:melk" >> ${output_file}
echo "brood:volkoren" >> ${output_file}
echo "koffie:koffiebonen" >> ${output_file}
echo "groente:appel, banaan" >> ${output_file}
echo "kwark:alpro perzik" >> ${output_file}
echo "kwark:alpro aardbei" >> ${output_file}
echo "kwark:alpro kersen" >> ${output_file}
echo "kwark:alpro bosbessen" >> ${output_file}
echo "boter:danio bosbessen" >> ${output_file}
echo "boter:danio kersen" >> ${output_file}
echo "boter:danio kiwi" >> ${output_file}

echo results in ${output_file}

open ${output_file}
