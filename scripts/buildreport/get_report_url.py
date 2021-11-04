import os, requests

def get_permanent_download_url():

    ARTIFACT_URL_MAP = os.environ['BITRISE_PERMANENT_DOWNLOAD_URL_MAP']
    
    if ARTIFACT_URL_MAP is not None:
        artifacts = ARTIFACT_URL_MAP.split("|")
        for artifact in artifacts:
            if "profile.zip" in artifact:
                return artifact.split("=>")[1]
    return None

def get_expiring_download_url():

    BITRISE_API_ACCESS_TOKEN = os.environ['BITRISE_API_ACCESS_TOKEN']
    APP_SLUG = os.environ['BITRISE_APP_SLUG']
    BUILD_SLUG = os.environ['BITRISE_BUILD_SLUG']

    BASE_URL = "https://api.bitrise.io/v0.1"
    ARTIFACT_BASE_URL = BASE_URL + "/apps/" + APP_SLUG + "/builds/" + BUILD_SLUG + "/artifacts"
    HEADERS = {
        "Authorization": BITRISE_API_ACCESS_TOKEN,
        "Content-Type": "application/json"
    }

    response = requests.get(ARTIFACT_BASE_URL, headers=HEADERS)
    if response.status_code == 200:
        artifact_list_response = response.json()
        for artifact in artifact_list_response['data']:
            if artifact['title'] == 'profile.zip':
                artifact_url = ARTIFACT_BASE_URL + "/" + artifact['slug']
                response = requests.get(artifact_url, headers=HEADERS)
                if response.status_code == 200:
                    artifact_response = response.json()
                    return artifact_response['data']['expiring_download_url']