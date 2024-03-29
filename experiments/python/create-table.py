#
# Re-create table for use in tests
#

from util import log, resource

table_name = 'shop'


def delete_table():
    log(f'Deleting {table_name}')
    try:
        table = resource.Table(table_name)
        table.delete()
        table.wait_until_not_exists()
    except:
        log(f'assuming {table_name} was deleted')


def create_table():
    params = {
        'TableName': table_name,
        'AttributeDefinitions': [
          {'AttributeName': 'PK', 'AttributeType': 'S'},
          {'AttributeName': 'SK', 'AttributeType': 'S'},
          {'AttributeName': 'GSI1PK', 'AttributeType': 'S'},
          {'AttributeName': 'GSI1SK', 'AttributeType': 'S'},
          {'AttributeName': 'GSI2PK', 'AttributeType': 'S'},
          {'AttributeName': 'GSI2SK', 'AttributeType': 'S'}
        ],
        'KeySchema': [
          {'AttributeName': 'PK', 'KeyType': 'HASH'},
          {'AttributeName': 'SK', 'KeyType': 'RANGE'}
        ],
        'GlobalSecondaryIndexes': [
          {
            'IndexName': 'GSI1',
            'KeySchema': [
              {'AttributeName': 'GSI1PK', 'KeyType': 'HASH'},
              {'AttributeName': 'GSI1SK', 'KeyType': 'RANGE'}
            ],
            'Projection': {'ProjectionType': 'ALL'}
          },
          {
            'IndexName': 'GSI2',
            'KeySchema': [
              {'AttributeName': 'GSI2PK', 'KeyType': 'HASH'},
              {'AttributeName': 'GSI2SK', 'KeyType': 'RANGE'}
            ],
            'Projection': {'ProjectionType': 'ALL'}
          }
        ],
        'BillingMode': 'PAY_PER_REQUEST',
    }

    table = resource.create_table(**params)
    log(f"Creating {table_name}")
    table.wait_until_exists()
    return table


if __name__ == '__main__':
    delete_table()
    create_table()
