<!-- (c) https://github.com/MontiCore/monticore -->

Due to MontiCore bootstrapping itself via the grammar and cd DSLs,
creating the next snapshot version is always a hassle.
This directory aims to catch breaking changes of the new version early
and collects patches for the next release.

To this aim, git patches with the required changes are collected in this directory.

> This document is still a work in progress

## Your PR breaks the next MontiCore release

Please create a patch for your changes,
such that the next MontiCore release is easier.

Work in your git branch, but please do not have any local, uncommited changes.
You can reset your local working tree via `git reset --hard yourBranchName`

Apply all previous patches: `./prepare-next-release/applyPatches.sh`
(this applies all patch files and switches to a new branch)

Modify the source files and ensure that the previously failing tests now work.
Then create a commit as usual
(e.g., `git add <file>` & `git commit -m <msg>`).
To build the patch file and change back to your original branch,
use: `./prepare-next-release/buildPatches.sh`

We can't commit and push the soon-to-be-required changes to the repository.
Instead, add and commit the patch file to your branch.
(This patch file will now be used after building the next release.)
(You can ignore the changes to the hash of the previous patch files.)

_Note: Any breaking changes in upstream projects may trigger the build jobs failure._

## Post-Release work

Congratulations, you have just released MontiCore, etc.

* Delete the first patch file (as it uses the snapshot version instead of full releases)
* Next, apply all remaining patches & push them.
* Remove all patch files & recreate the first patch, by using the next snapshot version.
  The first patch, 0001-..., should always update the various versions.

