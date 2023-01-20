from datetime import datetime
import boto3
import json

def log(s):
    print(datetime.utcnow().isoformat()[:-3] + 'Z: ' + s)

def logJson(s):
    s = s.replace('\'', '"')
    parsed = json.loads(s)
    log(json.dumps(parsed, indent=4))

client = boto3.client('dynamodb', endpoint_url='http://localhost:8000/', region_name='eu-central-1')
resource = boto3.resource('dynamodb', endpoint_url='http://localhost:8000/', region_name='eu-central-1')
# resource = boto3.resource('dynamodb')
# client = boto3.client('dynamodb')
