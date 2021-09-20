#!/usr/bin/env bash

sudo apt-get install -y jq
chmod +x ./scripts/buildreport/print_out_artifact_urls.sh
./scripts/buildreport/print_out_artifact_urls.sh | envman add --key ARTIFACT_URL