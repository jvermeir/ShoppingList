#
# Query some data by PK
#

from util import log, logJson, client, resource

table_name = 'shop'


def query_category1_and_ingredients():
    items = client.query(
        TableName=table_name,
        KeyConditionExpression="#PK = :PK",
        ExpressionAttributeNames={'#PK': 'PK'},
        ExpressionAttributeValues={':PK': {'S': 'CATEGORY#1'}}
    )
    log('CATEGORIES and INGREDIENTS')
    logJson(str(items['Items']))


def query_category1_without_details():
    items = client.query(
        TableName=table_name,
        KeyConditionExpression="#PK = :PK and #SK = :SK",
        ExpressionAttributeNames={'#PK': 'PK', '#SK': 'SK'},
        ExpressionAttributeValues={':PK': {'S': 'CATEGORY#1'}, ':SK': {'S': 'CATEGORY#1'}}
    )
    log('CATEGORIES and INGREDIENTS')
    logJson(str(items['Items']))


def query_vegetables_category():
    items = client.query(
        TableName=table_name,
        KeyConditionExpression="#PK = :PK",
        ExpressionAttributeNames={'#PK': 'PK'},
        ExpressionAttributeValues={':PK': {'S': 'CATEGORYNAME#vegetables'}}
    )
    log('CATEGORYNAME#vegetables')
    logJson(str(items['Items']))


def meta_data():
    table = resource.Table(table_name)
    log('Metadata')
    log(str(table))


if __name__ == '__main__':
    query_category1_and_ingredients()
    query_category1_without_details()
    query_vegetables_category()
    meta_data()
