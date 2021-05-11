curl --location --header "PRIVATE-TOKEN: rDyTjYZMfKBKsuWZzf1s" "https://git.rwth-aachen.de/api/v4/projects/monticore%2Fmdlinkchecker/jobs/artifacts/master/raw/target/libs/MDLinkChangerCLI.jar?job=build" --output MDLinkChangerCLI.jar
sh pre/mirror.sh
cd ..
if [ -d \"tmp\" ]; then rm -r tmp; fi
mkdir "tmp"
cd tmp
git clone git@github.com:MontiCore/monticore.git
cd ..
rm -r tmp/monticore/*
mv monticore/* tmp/monticore/
cd tmp/monticore
git add --all
(git commit -m 'Updated branch with most recent version.') || true
git push --force origin master
cd ..
cd ..
rm -r tmp
exit 0