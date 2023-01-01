#
# Scan some data
#

from boto3.dynamodb.conditions import Attr, ComparisonCondition

from util import resource, logJson, log

table_name = 'shop'
table = resource.Table(table_name)


def list_items(legend, filter_expression):
    log('Listing ' + legend)
    batch = table.scan(
        FilterExpression=filter_expression
    )
    logJson(str(batch))
    while 'LastEvaluatedKey' in batch:
        batch = table.scan(
            FilterExpression=filter_expression,
            ExclusiveStartKey=batch['LastEvaluatedKey'],

        )
        logJson(str(batch))


if __name__ == '__main__':
    list_items('all', Attr("PK").contains("#"))
    list_items('categories', Attr("PK").contains('CATEGORY#') and Attr("SK").contains('CATEGORY#'))
    list_items('ingredients', Attr("PK").contains('INGREDIENT#') and Attr("SK").contains('INGREDIENT#'))
    list_items('ingredients by ingredientName index', Attr("PK").contains('INGREDIENTNAME#'))
    list_items('ingredients for CATEGORY#1', Attr("PK").eq('CATEGORY#1') & Attr("SK").begins_with("INGREDIENT#"))
