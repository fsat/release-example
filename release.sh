#!/bin/bash

set -ex
set -x

git tag -f release-test-deleteme
git push -f origin release-test-deleteme
