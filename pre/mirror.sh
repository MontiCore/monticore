# (c) https://github.com/MontiCore/monticore 
# Delete "[[_TOC_]]" in all MD files before mirroring  
for file in $(find . -type f -name "*.md")
do 
  sed -i 's/\[\[_TOC_\]\]//' $file
done
echo "[INFO] Removed all occureneces of '[[_TOC_]]' in *.md files"
# Replace descriptions of not publicly reachable RWTH GitLab links
for file in $(find . -type f -name "*.md")
do
  content=$(java -jar MDLinkChangerCLI.jar -f $file)
  echo "$content" > $file
done
echo "[INFO] Replaced descriptions of not publicly available GitLab links"
