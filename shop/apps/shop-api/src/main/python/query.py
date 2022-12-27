#
# Query some data
#

import boto3

from util import log

client = boto3.client('dynamodb', endpoint_url='http://localhost:8000/')

# dynamodb = boto3.resource('dynamodb')
table_name = 'shop'


def query_1():
    items = client.query(
        TableName=table_name,
        KeyConditionExpression="#PK = :pk",
        ExpressionAttributeNames={'#PK': 'PK'},
        ExpressionAttributeValues={':pk': {'S': 'CATEGORY#1'}}
    )
    log('Category#1')
    log(str(items['Items'][0]))


def query_vegetables_category():
    items = client.query(
        TableName=table_name,
        KeyConditionExpression="#PK = :pk",
        ExpressionAttributeNames={'#PK': 'PK'},
        ExpressionAttributeValues={':pk': {'S': 'CATEGORYNAME#vegetables'}}
    )
    log('Categoryname#1')
    log(str(items['Items'][0]))


if __name__ == '__main__':
    query_1()
    query_vegetables_category()
