#! /bin/bash
# (c) https://github.com/MontiCore/monticore
# Script for creating new patch files


currentBranch=`git rev-parse --abbrev-ref HEAD`
originalBranch="${currentBranch/-crystal-ball/}"

echo "Creating patch difference to $originalBranch"
git format-patch --no-stat --minimal -N -o .\prepare-next-release\ $originalBranch --
if [ "$?" != "0" ]; then
    echo "Failed to create patch differences"
    exit 1
fi
echo "Switching back to branch: $originalBranch"
git checkout $originalBranch
