#
# See what happens if we try to insert records with the same PK but a different SK.
#

from util import log, resource, client, logJson

table_name = 'shop'
count = 0


def test(pk,sk):
    log('pk: {pk}, sk: {sk}'.format(pk=pk, sk=sk))
    table = resource.Table(table_name)
    table.put_item(
        TableName=table_name,
        Item={
            'PK': 'category#{pk}'.format(pk=pk),
            'SK': 'category#{sk}'.format(sk=sk)
        },
        ConditionExpression='attribute_not_exists(PK)'
    )


def query(PK):
    items = client.query(
        TableName=table_name,
        KeyConditionExpression="#PK = :PK",
        ExpressionAttributeNames={'#PK': 'PK'},
        ExpressionAttributeValues={':PK': {'S': 'category#{id}'.format(id=PK)}}
    )
    logJson(str(items['Items']))

if __name__ == '__main__':
    test('1', '1')
    query('1')
    test('1', '2')
    query('1')
