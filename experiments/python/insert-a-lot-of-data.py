#
# Insert a lot of data, so we can try and find out if queries are efficient.
# In real life we'll only have 30+ categories and maybe 200 ingredient. For the
# purpose of this test they get a sequence number. The test will create thousands, so we can
# try to retrieve a single category or ingredient from a large set.
#
# The test is not very useful, because
# Dynamo will not allow queries without using a key. The test was iffy anyway because the real infrastructure for Dynamo
# is not comparable to a local Docker based setup.
#

from util import log, resource, client, logJson

table_name = 'shop'
count = 0


def log_progress():
    global count
    count += 1
    if count % 1000 == 0:
        log('count: {i}'.format(i=count))


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
                }
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
        ExpressionAttributeValues={':PK': {'S': 'CATEGORY#{id}'.format(id=id)}}
    )
    logJson(str(items['Items']))


if __name__ == '__main__':
    # insert_data(200, 500)
    query('1')
    query('2')
