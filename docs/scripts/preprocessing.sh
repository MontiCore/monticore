#!/bin/bash
# (c) https://github.com/MontiCore/monticore  
# script for all preprocessing steps of the pages job
# is used to have uniform bases for both gitlab and github pages
#
#
# old scripts:
# execute report scripts and print output to *.md file, to use these in pages
#sh docs/scripts/errorList.sh '../../' 'target/site/errorList' > docs/scripts/ErrorList.md
#sh docs/scripts/detailedErrorList.sh '../../' 'target/site/detailedErrorList' > docs/scripts/DetailedErrorList.md
#sh docs/scripts/findDoubleFileNames.sh './' 'target/site/findDoubleFileNames' > docs/scripts/FindDoubleFileNames.md
#sh docs/scripts/ftlAnalysis.sh './' 'configure.StartAllOutput' 'target/site/ftlAnalysis' > docs/scripts/FtlAnalysis.md
#echo "[INFO] Executed report scripts for pages"
#
# remove all occurrences of '[[_TOC_]]' in markdown files
# because mkdocs already renders its own toc
case " $* " in
  *" inplace "*)
  for file in $(find ./docs/docs -type f -name "*.md")
  do
    sed -i 's/\[\[_TOC_\]\]//' $file
    perl -pi -e 's/\[([^\[\]\(\)]*)\]\([^\[\]\(\)]*git.rwth-aachen.de[^\[\]\(\)]*?\)/$1/g' $file
  done
  echo "[INFO] Removed all occurrences of '[[_TOC_]]' in *.md files"
  echo "[INFO] Removed all links to https://git.rwth-aachen.de in *.md files"
    ;;
esac
# move all directories that contain *.md files to the docs folder
# because mkdocs can only find *.md files there
rm -r docs_wd || true

case " $* " in
  *" symlink "*)
    # use symlinks to track updates
    mkdir docs_wd
    ln -s ../docs/overrides docs_wd/
    ln -s ../docs/stylesheets docs_wd/
    ln -s ../docs/scripts docs_wd/
    ln -s ../docs/img docs_wd/
    # all images referenced in the root-Readme must be handled specially :(
    mkdir -p docs_wd/docs/img
    ln -s ../../../docs/img/MC_Symp_Banner.png docs_wd/docs/img/MC_Symp_Banner.png
    ln -s ../docs/README.md docs_wd/
    # Link to the javadoc directories
    mkdir -p docs_wd/monticore-runtime
    ln -s ../../monticore-runtime/target/docs/javadoc docs_wd/monticore-runtime/
    ln -s ../../monticore-runtime/target/docs/testFixturesJavadoc docs_wd/monticore-runtime/
    mkdir -p docs_wd/monticore-grammar
    ln -s ../../monticore-grammar/target/docs/javadoc docs_wd/monticore-grammar/
    ln -s ../../monticore-grammar/target/docs/testFixturesJavadoc docs_wd/monticore-grammar/
    echo "[INFO] Using symlinks for live editing"
    ;;
  *)
    cp -r docs docs_wd
    rm docs_wd/*.md
    cp docs/README.md docs_wd/README.md
    # all images referenced in the root-Readme must be handled specially :(
    mkdir -p docs_wd/docs/img
    cp docs/img/MC_Symp_Banner.png docs_wd/docs/img/MC_Symp_Banner.png
    echo "[INFO] Copied site design"
    # Copy the javadoc directories
    mkdir -p docs_wd/monticore-runtime
    cp -r monticore-runtime/target/docs/javadoc docs_wd/monticore-runtime/javadoc
    cp -r monticore-runtime/target/docs/testFixturesJavadoc docs_wd/monticore-runtime/testFixturesJavadoc
    mkdir -p docs_wd/monticore-grammar
    cp -r monticore-grammar/target/docs/javadoc docs_wd/monticore-grammar/javadoc
    cp -r monticore-grammar/target/docs/testFixturesJavadoc docs_wd/monticore-grammar/testFixturesJavadoc
    echo "[INFO] Copied JavaDocs"
    ;;
esac

for SOURCE_DIR in "00.org" "docs" "monticore-grammar/src" "monticore-libraries/javagen-runtime" "monticore-runtime/src"; do
  # We link to java & mc4 files in our md files - which is why we have to redirect them too
  find "$SOURCE_DIR" -type f \( -name "*.md" \) | while read -r filepath; do
     target_file="docs_wd/$filepath"
     mkdir -p "$(dirname "$target_file")"
     # use snippets to include the original files content
     if [ ! -f "$target_file" ]; then
       echo "--8<-- \"$filepath\"" > "$target_file"
     fi
  done
done
echo "[INFO] Created snippet files"

# the landing page snippet has to be removed again
rm docs_wd/docs/README.md
