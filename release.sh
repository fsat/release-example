#!/bin/bash

set -e

openssl aes-256-cbc -K $encrypted_7dbe1e2dc648_key -iv $encrypted_7dbe1e2dc648_iv -in .travis/travis-gh.enc -out .travis/travis-gh -d
eval "$(ssh-agent -s)"
ls -l .travis/
chmod go-rwx .travis/travis-gh
chmod u-wx .travis/travish-gh
ssh-add .travis/travis-gh
rm .travis/travis-gh

set -x

git tag -f release-test-deleteme
git push -f origin release-test-deleteme
