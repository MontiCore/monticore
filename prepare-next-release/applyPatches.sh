#! /bin/bash
# (c) https://github.com/MontiCore/monticore
# Script for applying all patches to check if we are about to break the next release of MontiCore

# Start by switching to a new branch (using the suffix -crystal-ball)
currentBranch=`git rev-parse --abbrev-ref HEAD`

if [[ "$currentBranch" == *"-crystal-ball"* ]]; then
  echo "Script may not be executed on a branch with applied patches!"
  exit 1
fi

git checkout -b $currentBranch-crystal-ball
if [ "$?" != "0" ]; then
    echo "Failed to switch to new branch $currentBranch-crystal-ball"
    exit 1
fi
# And apply all patches
git am --abort >/dev/null 2>&1
git am --3way prepare-next-release/*.patch
if [ "$?" != "0" ]; then
    echo "Failed to apply the patches."
    echo "Please consult the 'PreparingTheNextRelease' documentation "
    exit 1
else
    echo "Cleanly applied patches"
fi