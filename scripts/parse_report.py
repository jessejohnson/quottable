import glob, os
from bs4 import BeautifulSoup

REPORT_PATH = "../build/reports/profile/"
os.chdir(REPORT_PATH)
REPORT_FILE = glob.glob("*.html")[0]

with open(REPORT_FILE) as html_report:
    soup = BeautifulSoup(html_report, 'html.parser')

def parse_report():
    last_table = soup.find_all('table')[-1]
    all_rows = last_table.find_all('tr')
    print("*Module Build Times*\n=============")
    print("Module       Duration")
    for row in all_rows[1:-1]:
        first_cell = row.td
        if not first_cell.attrs:
            module_name = row.find_all('td')[0].string
            duration = row.find_all('td')[1].string
            print("{0}      {1}".format(module_name, duration))

parse_report()