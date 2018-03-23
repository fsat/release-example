#!/bin/bash

# Credit goes to https://www.planetholt.com/2017/04/03/automated-releases-using-sbt-release-travis-ci/6196
# Ideas behind the git deploy keys + separate SSH based remote came from the site above.
#
# If using paid Travis, simply add the private key for your deploy key pair into Travis and this script won't be required.

set -ex
set -o errexit -o nounset

RELEASE_FROM_BRANCH=$1
RELEASE_FROM_REMOTE=release

RELEASE_USER_NAME="Felix Satyaputra"
RELEASE_USER_EMAIL=fsatyaputra@gmail.com

GIT_REPO=git@github.com:fsat/release-example.git

if [[ "$RELEASE_FROM_BRANCH" == "" ]]
then
  echo "ERROR: release branch is not specified"
  exit 1
fi

if [[ "$TRAVIS" != "true" ]]
then
  echo "ERROR: This script can only be run in Travis"
  exit 1
fi

if [[ "$TRAVIS_BRANCH" != "$RELEASE_FROM_BRANCH" ]]
then
  echo "ERROR: Only supporting release from branch [$RELEASE_FROM_BRANCH] - trying to release from branch [$TRAVIS_BRANCH] instead"
  exit 1
fi

if [[ "$TRAVIS_PULL_REQUEST" != "false" ]]
then
  echo "ERROR: trying to release from PR builds is not allowed"
  exit 1
fi

echo "Set credentials to user having the deploy key"
git config user.name "$RELEASE_USER_NAME"
git config user.email $RELEASE_USER_EMAIL

echo "Reset to [$RELEASE_FROM_BRANCH] branch"
git remote add $RELEASE_FROM_REMOTE $GIT_REPO
git fetch $RELEASE_FROM_REMOTE

# Cleanup all the remaining artifacts
git reset --hard
git clean -dxf

# Swap to release branch and point to release remote
git checkout $RELEASE_FROM_BRANCH
git branch --set-upstream-to=$RELEASE_FROM_REMOTE/$RELEASE_FROM_BRANCH


echo "Releasing"
sbt "release with-defaults"
