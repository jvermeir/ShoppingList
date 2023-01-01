#
# Insert some test data
# Then query by PK and by GSI's
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
                        'SK': {'S': 'INGREDIENT#1'},
                        'id': {'S': '1'},
                        'name': {'S': 'tomatoes'},
                        'categoryId': {'S': '1'},
                        'GSI1PK': {'S': 'INGREDIENT#1'},
                        'GSI1SK': {'S': 'INGREDIENT#1'},
                        'GSI2PK': {'S': 'INGREDIENTNAME#tomatoes'},
                        'GSI2SK': {'S': 'INGREDIENTNAME#tomatoes'},
                    },
                    'ConditionExpression': 'attribute_not_exists(PK)'
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
                    'ConditionExpression': 'attribute_not_exists(PK)'
                }
            },

            {
                'Put': {
                    'TableName': table_name,
                    'Item': {
                        'PK': {'S': 'CATEGORY#1'},
                        'SK': {'S': 'INGREDIENT#3'},
                        'id': {'S': '3'},
                        'name': {'S': 'mushrooms'},
                        'categoryId': {'S': '1'},
                        'GSI1PK': {'S': 'INGREDIENT#3'},
                        'GSI1SK': {'S': 'INGREDIENT#3'},
                        'GSI2PK': {'S': 'INGREDIENTNAME#mushrooms'},
                        'GSI2SK': {'S': 'INGREDIENTNAME#mushrooms'},
                    },
                    'ConditionExpression': 'attribute_not_exists(PK)'
                },
            },
            {
                'Put': {
                    'TableName': table_name,
                    'Item': {
                        'PK': {'S': 'INGREDIENTNAME#mushrooms'},
                        'SK': {'S': 'INGREDIENTNAME#mushrooms'},
                        'id': {'S': '1'},
                        'name': {'S': 'mushrooms'},
                    },
                    'ConditionExpression': 'attribute_not_exists(PK)'
                }
            },

            {
                'Put': {
                    'TableName': table_name,
                    'Item': {
                        'PK': {'S': 'CATEGORY#2'},
                        'SK': {'S': 'INGREDIENT#2'},
                        'id': {'S': '2'},
                        'name': {'S': 'spaghetti'},
                        'categoryId': {'S': '2'},
                        'GSI1PK': {'S': 'INGREDIENT#2'},
                        'GSI1SK': {'S': 'INGREDIENT#2'},
                        'GSI2PK': {'S': 'INGREDIENTNAME#spaghetti'},
                        'GSI2SK': {'S': 'INGREDIENTNAME#spaghetti'},
                    },
                    'ConditionExpression': 'attribute_not_exists(PK)'
                },
            },
            {
                'Put': {
                    'TableName': table_name,
                    'Item': {
                        'PK': {'S': 'INGREDIENTNAME#spaghetti'},
                        'SK': {'S': 'INGREDIENTNAME#spaghetti'},
                        'id': {'S': '3'},
                        'name': {'S': 'spaghetti'},
                    },
                    'ConditionExpression': 'attribute_not_exists(PK)'
                }
            },
        ])
    log(str(response))


def query_by_pk(category_id, ingredient_id):
    items = client.query(
        TableName=table_name,
        KeyConditionExpression='#PK = :PK and #SK = :SK',
        ExpressionAttributeNames={'#PK': 'PK', '#SK': 'SK'},
        ExpressionAttributeValues={':PK': {'S': '{id}'.format(id=category_id)}, ':SK': {'S': '{id}'.format(id=ingredient_id)}}
    )
    log(f'INGREDIENTS by pk = {category_id} and sk = {ingredient_id}')
    logJson(str(items['Items']))


def query_by_gsi(ingredient_name):
    items = client.query(
        TableName=table_name,
        IndexName='GSI2',
        ProjectionExpression='PK, SK, id, #name, categoryId',
        KeyConditionExpression='#GSI2PK = :GSI2PK',
        ExpressionAttributeNames={'#GSI2PK': 'GSI2PK', '#name': 'name'},
        ExpressionAttributeValues={':GSI2PK': {'S': f'INGREDIENTNAME#{ingredient_name}'}}
    )

    log('INGREDIENTS by gsi')
    logJson(str(items['Items']))


if __name__ == '__main__':
    insert_categories()
    insert_ingredients()
    query_by_pk('CATEGORY#1', 'INGREDIENT#1')
    query_by_gsi('spaghetti')
