#!/bin/bash
set -ex

pip install beautifulsoup4 requests
python2 ./scripts/buildreport/script.py ./build/reports/profile/
