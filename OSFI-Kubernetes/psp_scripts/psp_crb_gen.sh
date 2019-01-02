path=/root/psp_script/sa.txt
cat $path | while read -r var
do
  sed -e "s/replace_me/${var}/g" admin-default-crb.yaml | kubectl apply -f -
done
