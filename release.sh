#!/bin/bash

set -ex

RELEASE_FROM_BRANCH=release-from-travis
RELEASE_FROM_REMOTE=release

# TODO: fail if not on the expected branch

echo "Set credentials to release user"
git config user.name "Felix Satyaputra"
git config user.email fsatyaputra@gmail.com

echo "Reset to release branch"
git remote add $RELEASE_FROM_REMOTE git@github.com:fsat/release-example.git
git fetch release
git reset --hard
git checkout -b $RELEASE_FROM_BRANCH $RELEASE_FROM_REMOTE/$RELEASE_FROM_BRANCH

# TODO: actual release

git tag -f release-test-deleteme
git push -f $RELEASE_FROM_REMOTE release-test-deleteme
