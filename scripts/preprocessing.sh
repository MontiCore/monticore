#!/bin/bash
# (c) https://github.com/MontiCore/monticore  
# script for all preprocessing steps of the pages job
# is used to have uniform bases for both gitlab and github pages
# is used from '.gitlab-ci.yml'(gitlab) and '.travis.yml'(github)
#
# execute report scripts and print output to *.md file, to use these in pages
#sh docs/scripts/errorList.sh '../../' 'target/site/errorList' > docs/scripts/ErrorList.md
#sh docs/scripts/detailedErrorList.sh '../../' 'target/site/detailedErrorList' > docs/scripts/DetailedErrorList.md
#sh docs/scripts/findDoubleFileNames.sh './' 'target/site/findDoubleFileNames' > docs/scripts/FindDoubleFileNames.md
#sh docs/scripts/ftlAnalysis.sh './' 'configure.StartAllOutput' 'target/site/ftlAnalysis' > docs/scripts/FtlAnalysis.md
#echo "[INFO] Executed report scripts for pages"
#
# move all directories that contain *.md files to the docs folder
# because mkdocs can only find *.md files there
mkdir docs/docs
mv docs/*.md docs/docs
mv docs/further_docs docs/docs/further_docs
mv monticore-grammar docs/monticore-grammar
mv monticore-runtime docs/monticore-runtime
mv 00.org docs/00.org
mv *.md docs/
mv *.png docs/
mv img/ docs/
echo "[INFO] Moved *.md files to 'docs' folder"
#
# remove all occurences of '[[_TOC_]]' in markdown files
# because mkdocs already renders its own toc
for file in $(find ./docs -type f -name "*.md")
do 
  sed -i 's/\[\[_TOC_\]\]//' $file
  perl -pi -e 's/\[([^\[\]\(\)]*)\]\([^\[\]\(\)]*git.rwth-aachen.de[^\[\]\(\)]*?\)/$1/g' $file
done
echo "[INFO] Removed all occurrences of '[[_TOC_]]' in *.md files"
echo "[INFO] Removed all links to https://git.rwth-aachen.de in *.md files"
