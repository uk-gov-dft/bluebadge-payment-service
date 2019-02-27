#!/usr/bin/env bash

curl -s -o run-regression-script.sh -H "Authorization: token ${GITHUB_TOKEN}" -H 'Accept: application/vnd.github.v3.raw' -O -L https://raw.githubusercontent.com/uk-gov-dft/shell-scripts/master/run-regression.sh

./run-regression-script.sh
