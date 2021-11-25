import requests

def get_workflow_artifacts():
    APP_SLUG = os.environ['BITRISE_APP_SLUG']
    BUILD_SLUGS = os.environ['ROUTER_STARTED_BUILD_SLUGS'].splitlines()
    BITRISE_API_ACCESS_TOKEN = os.environ['BITRISE_API_ACCESS_TOKEN']

    BASE_URL = "https://api.bitrise.io/v0.1"
    BUILD_URL = BASE_URL + "/apps/" + APP_SLUG + "/builds/"

    for slug in BUILD_SLUGS:
        url = BUILD_URL+slug+"/artifacts"
        HEADERS = {
        "Authorization": BITRISE_API_ACCESS_TOKEN,
        "Content-Type": "application/json"
        }
        # we're not checking build status
        response = requests.get(url, headers=HEADERS)
        if response.status_code == 200:
            artifact_list_response = response.json()
