#
# Insert some test data
#

import boto3

from util import log

dynamodb = boto3.resource('dynamodb', endpoint_url='http://localhost:8000/')
client = boto3.client('dynamodb', endpoint_url='http://localhost:8000/')

# dynamodb = boto3.resource('dynamodb')
table_name = 'shop'


def insert_categories():
    response = client.transact_write_items(
      TransactItems=[
        {
          "Put": {
            "TableName": table_name,
            "Item": {
              "PK": {"S": "CATEGORY#1"},
              "SK": {"S": "CATEGORY#1"},
              "id": {"S": "1"},
              "name": {"S": "vegetables"},
              "shopOrder": {"S": "1"}
            },
            "ConditionExpression": "attribute_not_exists(PK)",
          },
        },
        {
          "Put": {
            "TableName": table_name,
            "Item": {
              "PK": {"S": "CATEGORYNAME#vegetables"},
              "SK": {"S": "CATEGORYNAME#vegetables"},
              "id": {"S": "1"},
              "name": {"S": "vegetables"},
            },
          }
        },
        {
          "Put": {
            "TableName": table_name,
            "Item": {
              "PK": {"S": "CATEGORY#2"},
              "SK": {"S": "CATEGORY#2"},
              "id": {"S": "2"},
              "name": {"S": "pasta"},
              "shopOrder": {"S": "2"}
            },
            "ConditionExpression": "attribute_not_exists(PK)",
          }
        },
        {
          "Put": {
            "TableName": table_name,
            "Item": {
              "PK": {"S": "CATEGORYNAME#pasta"},
              "SK": {"S": "CATEGORYNAME#pasta"},
              "id": {"S": "2"},
              "name": {"S": "pasta"},
            },
            "ConditionExpression": "attribute_not_exists(PK)",
          }
        }
      ]
    )
    log(str(response))


if __name__ == '__main__':
    insert_categories()
