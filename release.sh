#!/bin/bash

# Credit goes to https://www.planetholt.com/2017/04/03/automated-releases-using-sbt-release-travis-ci/6196
# Ideas behind the git deploy keys + separate SSH based remote came from the site above.
# If using paid Travis, simply add the private key for your deploy key pair into Travis

set -ex

RELEASE_FROM_BRANCH=release-from-travis
RELEASE_FROM_REMOTE=release

RELEASE_USER_NAME="Felix Satyaputra"
RELEASE_USER_EMAIL=fsatyaputra@gmail.com

GIT_REPO=git@github.com:fsat/release-example.git

# TODO: fail if not on the expected branch

echo "Set credentials to user having the deploy key"
git config user.name "$RELEASE_USER_NAME"
git config user.email $RELEASE_USER_EMAIL

echo "Reset to release branch"
git remote add $RELEASE_FROM_REMOTE $GIT_REPO
git fetch release
git reset --hard
git checkout -b $RELEASE_FROM_BRANCH $RELEASE_FROM_REMOTE/$RELEASE_FROM_BRANCH

# TODO: actual release

git tag -f release-test-deleteme
git push -f $RELEASE_FROM_REMOTE release-test-deleteme
