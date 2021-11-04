#!/bin/bash
set -ex

pip install beautifulsoup4 requests
python --version
python2 --version
python3 --version
python2 ./scripts/buildreport/script.py ./build/reports/profile/
