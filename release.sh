#!/bin/bash

set -e

openssl aes-256-cbc -K $encrypted_7dbe1e2dc648_key -iv $encrypted_7dbe1e2dc648_iv -in .travis/travis-gh.enc -out .travis/travis-gh -d
eval "$(ssh-agent -s)"

set -x

ls -l .travis/
chmod 0600 .travis/travis-gh
ssh-add .travis/travis-gh
rm .travis/travis-gh

git tag -f release-test-deleteme
git push -f origin release-test-deleteme
