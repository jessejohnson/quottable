#!/bin/bash
set -ex

pip2 install beautifulsoup4 requests
python ./scripts/buildreport/script.py ./build/reports/profile/
