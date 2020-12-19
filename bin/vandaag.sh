#!/bin/bash

export LC_ALL=nl_NL

if [ -z $1 ];
then
  start_date=$(date -v +0d +%d%m%Y)
else
  start_date=$(date -v +${1}d +%d%m%Y)
fi

today=$(date -j -f %d%m%Y ${start_date} +%d%m%Y)
file_name=$(date -j -f %d%m%Y ${start_date} +%d%m.txt)
week_day=$(date -j -f %d%m%Y ${start_date} +%A)
output_folder=../ShoppingListData/menus

mkdir -p ${output_folder}
output_file=${output_folder}/${file_name}
rm -rf ${output_file}
touch ${output_file}
echo "${week_day} valt op:${today}" > ${output_file}
for i in {0..6}; do
  NEXT_DATE=$(date -j -v +${i}d -f %d%m%Y ${today} +%A)
  echo ${NEXT_DATE}:- >> ${output_file}
done

echo "" >> ${output_file}
echo "extra" >> ${output_file}

atom ${output_file}
