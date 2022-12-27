from datetime import datetime


def log(s):
    print(datetime.utcnow().isoformat()[:-3] + 'Z: ' + s)
