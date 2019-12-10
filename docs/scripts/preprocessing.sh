#!/bin/bash
# script for all preprocessing steps of the pages job
# is used to have uniform bases for both gitlab and github pages
# is used from '.gitlab-ci.yml'(gitlab) and '.travis.yml'(github)
ls
# execute report scripts and print output to *.md file, to use these in pages
sh errorList.sh '../../' '../../target/site/errorList' > ../ErrorList.md
sh detailedErrorList.sh '../../' '../../target/site/detailedErrorList' > ../DetailedErrorList.md
sh findDoubleFileNames.sh './' 'target/site/findDoubleFileNames' > ../FindDoubleFileNames.md
sh ftlAnalysis.sh './' 'configure.StartAllOutput' 'target/site/ftlAnalysis' > ../FtlAnalysis.md

# move all directories that contain *.md files to the docs folder
# because mkdocs can only find *.md files there
mv ../../monticore-grammar ../monticore-grammar
mv ../../monticore-runtime ../monticore-runtime
mv ../../00.org ../00.org
mv ../../*.md ../