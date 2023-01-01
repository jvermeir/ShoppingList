#
# Insert some test data
# Then query by PK
#

from util import log, client, logJson

table_name = 'shop'


def insert_categories():
    response = client.transact_write_items(
        TransactItems=[
            {
                'Put': {
                    'TableName': table_name,
                    'Item': {
                        'PK': {'S': 'CATEGORY#1'},
                        'SK': {'S': 'CATEGORY#1'},
                        'id': {'S': '1'},
                        'name': {'S': 'vegetables'},
                        'shopOrder': {'S': '1'}
                    },
                },
            },
            {
                'Put': {
                    'TableName': table_name,
                    'Item': {
                        'PK': {'S': 'CATEGORYNAME#vegetables'},
                        'SK': {'S': 'CATEGORYNAME#vegetables'},
                        'id': {'S': '1'},
                        'name': {'S': 'vegetables'},
                    },
                }
            },
            {
                'Put': {
                    'TableName': table_name,
                    'Item': {
                        'PK': {'S': 'CATEGORY#2'},
                        'SK': {'S': 'CATEGORY#2'},
                        'id': {'S': '2'},
                        'name': {'S': 'pasta'},
                        'shopOrder': {'S': '2'}
                    },
                }
            },
            {
                'Put': {
                    'TableName': table_name,
                    'Item': {
                        'PK': {'S': 'CATEGORYNAME#pasta'},
                        'SK': {'S': 'CATEGORYNAME#pasta'},
                        'id': {'S': '2'},
                        'name': {'S': 'pasta'},
                    },
                }
            }
        ]
    )
    log(str(response))


def insert_ingredients():
    response = client.transact_write_items(
        TransactItems=[
            {
                'Put': {
                    'TableName': table_name,
                    'Item': {
                        'PK': {'S': 'CATEGORY#1'},
                        'SK': {'S': '#INGREDIENT#1'},
                        'id': {'S': '1'},
                        'name': {'S': 'tomatoes'},
                        'categoryId': {'S': '1'},
                    },
                },
            },
            {
                'Put': {
                    'TableName': table_name,
                    'Item': {
                        'PK': {'S': 'INGREDIENTNAME#tomatoes'},
                        'SK': {'S': 'INGREDIENTNAME#tomatoes'},
                        'id': {'S': '1'},
                        'name': {'S': 'tomatoes'},
                    },
                },
            },

            {
                'Put': {
                    'TableName': table_name,
                    'Item': {
                        'PK': {'S': 'CATEGORY#1'},
                        'SK': {'S': '#INGREDIENT#2'},
                        'id': {'S': '2'},
                        'name': {'S': 'mushrooms'},
                        'categoryId': {'S': '1'},
                    },
                },
            },
            {
                'Put': {
                    'TableName': table_name,
                    'Item': {
                        'PK': {'S': 'INGREDIENTNAME#mushrooms'},
                        'SK': {'S': 'INGREDIENTNAME#mushrooms'},
                        'id': {'S': '2'},
                        'name': {'S': 'tomatoes'},
                    },
                },
            },

            {
                'Put': {
                    'TableName': table_name,
                    'Item': {
                        'PK': {'S': 'CATEGORY#2'},
                        'SK': {'S': '#INGREDIENT#3'},
                        'id': {'S': '3'},
                        'name': {'S': 'spaghetti'},
                        'categoryId': {'S': '2'},
                    },
                },
            },
            {
                'Put': {
                    'TableName': table_name,
                    'Item': {
                        'PK': {'S': 'INGREDIENTNAME#spaghetti'},
                        'SK': {'S': 'INGREDIENTNAME#spaghetti'},
                        'id': {'S': '2'},
                        'name': {'S': 'spaghetti'},
                    },
                },
            },
        ])

    log(str(response))


def query_by_pk_and_sk(category_id, ingredient_id):
    items = client.query(
        TableName=table_name,
        KeyConditionExpression='#PK = :PK and #SK = :SK',
        ExpressionAttributeNames={'#PK': 'PK', '#SK': 'SK'},
        ExpressionAttributeValues={':PK': {'S': '{id}'.format(id=category_id)}, ':SK': {'S': '{id}'.format(id=ingredient_id)}}
    )
    log(f'INGREDIENTS by pk = {category_id} and sk = {ingredient_id}')
    logJson(str(items['Items']))


def query_all():
    items = client.scan(
        TableName=table_name,
    )
    log('all data')
    logJson(str(items['Items']))


if __name__ == '__main__':
    insert_categories()
    insert_ingredients()
    query_all()
    query_by_pk_and_sk('CATEGORY#1', '#INGREDIENT#1')
    # aha! no query by ingredient name.