#!/bin/bash
set -ex

pip install --verbose beautifulsoup4 requests
pip2 --version
pip --version
python --version
python2 --version
python3 --version
python2 ./scripts/buildreport/script.py ./build/reports/profile/
