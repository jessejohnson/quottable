#!/bin/bash
set -ex

curl https://bootstrap.pypa.io/pip/2.7/get-pip.py --output get-pip.py
python get-pip.py
pip2 install --verbose beautifulsoup4 requests
pip2 --version
pip --version
python --version
python2 --version
python3 --version
python2 ./scripts/buildreport/script.py ./build/reports/profile/
