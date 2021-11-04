import os
import requests
import json

def send_slack_message(REPORT_SUMMARY, ARTIFACT_URL):

    SLACK_WEBHOOK_URL = os.environ['SLACK_WEBHOOK_URL']
    BITRISE_BUILD_URL = os.environ['BITRISE_BUILD_URL']
    APP_TITLE = os.environ['BITRISE_APP_TITLE']
    BRANCH = os.environ['BITRISE_GIT_BRANCH']
    WORKFLOW = os.environ['BITRISE_TRIGGERED_WORKFLOW_ID']

    data = {
        "text": ":wave: Profile Report",
        "attachments": [
            {
                "blocks": [
                    {
                        "type": "section",
                        "text": {
                            "type": "mrkdwn",
                            "text": REPORT_SUMMARY
                        }
                    },
                    {
                        "type": "divider"
                    },
                    {
                        "type": "actions",
                        "elements": [
                            {
                                "type": "button",
                                "text": {
                                    "type": "plain_text",
                                    "text": "View Build",
                                },
                                "value": "view_build",
                                "url": BITRISE_BUILD_URL
                            },
                            {
                                "type": "button",
                                "text": {
                                    "type": "plain_text",
                                    "text": "Download Report",
                                },
                                "value": "download_report",
                                "url": ARTIFACT_URL
                            }
                        ]
                    },
                    {
                        "type": "context",
                        "elements": [
                            {
                                "type": "plain_text",
                                "text": "{} | {} | {} workflow".format(APP_TITLE, BRANCH, WORKFLOW)
                            }
                        ]
                    }
                ]
            }
        ]
    }
    headers = {"Content-Type": "application/json"}
    result = requests.post(SLACK_WEBHOOK_URL, headers=headers, data=json.dumps(data))
    
    if result.status_code == 200:
        print("A report summary was sent to Slack!")
    else:
        print("Slack webhook failed with {} {}".format(result.status_code, result.text))