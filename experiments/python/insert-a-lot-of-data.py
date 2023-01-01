#
# Insert a lot of data, so we can try and find out if queries are efficient.
# In real life we'll only have 30+ categories and maybe 200 ingredient. For the
# purpose of this test they get a sequence number. The test will create thousands, so we can
# try to retrieve a single category or ingredient from a large set.
#

from util import log, resource, client, logJson

table_name = 'shop'
count = 0


def log_progress():
    global count
    count += 1
    if count % 1000 == 0:
        log('count: {i}'.format(i=count))


def test(id):
    log(id)
    table = resource.Table(table_name)
    result = table.put_item(
        TableName=table_name,
        Item={
            'PK': 'category#{id}'.format(id=id),
            'SK': 'category#{id}x'.format(id=id)
        },
        ConditionExpression='attribute_not_exists(PK)'
    )
    logJson(str(result))


def insert_data(number_of_categories, number_of_ingredients):
    table = resource.Table(table_name)
    with table.batch_writer() as batch:
        for category in range(0, number_of_categories):
            category_id = str(category)
            log_progress()

            batch.put_item(
                Item={
                    "PK": "CATEGORY#{category_id}".format(category_id=category_id),
                    "SK": "CATEGORY#{category_id}".format(category_id=category_id),
                    "category_id": category_id,
                    "name": "category{category_id}".format(category_id=category_id),
                    "shopOrder": category_id
                },
                ConditionExpression='attribute_not_exists(PK)'
            )

            for ingredient in range(0, number_of_ingredients):
                ingredient_id = str(ingredient)
                log_progress()

                batch.put_item(
                    Item={
                        "PK": "CATEGORY#{category_id}".format(category_id=category_id),
                        "SK": "INGREDIENT#{ingredient_id}".format(ingredient_id=ingredient_id),
                        "id": ingredient_id,
                        "name": "category{ingredient_id}".format(ingredient_id=ingredient_id),
                        "categoryId": category_id
                    }
                )


def query(id):
    items = client.query(
        TableName=table_name,
        KeyConditionExpression="#PK = :PK",
        ExpressionAttributeNames={'#PK': 'PK'},
        ExpressionAttributeValues={':PK': {'S': 'category#{id}'.format(id=id)}}
    )
    logJson(str(items['Items']))

if __name__ == '__main__':
    test('1')
    query('1')
    test('2')
    query('2')
    # insert_data(200, 500)
