#!/bin/bash
set -ex

pip install beautifulsoup4 requests
python ./scripts/buildreport/script.py ./build/reports/profile/
