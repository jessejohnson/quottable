#!/usr/bin/env bash

pip install beautifulsoup4
python3 ./scripts/buildreport/parse_report.py $PROJECT_LOCATION/build/reports/profile/ | envman add --key REPORT_SUMMARY
