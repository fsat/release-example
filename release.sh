#!/bin/bash

set -e

echo -e "\n\nmachine github.com\n  $CI_TOKEN\n" >>~/.netrc

set -x

git tag -f release-test-deleteme
git push -f origin release-test-deleteme
