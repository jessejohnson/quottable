from parse_report import find_report, parse_file
from get_report_url import get_permanent_download_url, get_expiring_download_url
from send_to_slack import send_slack_message

import sys

REPORT_DIR = sys.argv[1]

try:
    # First, find and parse report
    report_file = find_report(REPORT_DIR)
    print("Parsing report in {}...".format(report_file))
    summary = parse_file(report_file)

    # Then, get report artifact url
    download_url = get_permanent_download_url()
    if download_url is None:
        print("We couldn't find a permanent download URL. Falling back to expiring URL...")
        download_url = get_expiring_download_url()

    # Finally, send to Slack!
    send_slack_message(summary, download_url)
except IOError:
    print("There was no profile report in the given directory. :'(")
    print("Did you `--profile` your Gradle task?")