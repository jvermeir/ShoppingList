import {getNameOfDayFromDate, getMonthAndDayFromDate, recalcDateForDayOfWeekFromStartOfPeriod, recalcDates} from "../menuFunctions";

test('name of day is "mon" on december 21st 2020', () => {
    expect(getNameOfDayFromDate(new Date(2020, 11, 21))).toBe("mon");
});

test('(month, day) is (12, 21) on december 21st 2020', () => {
    expect(getMonthAndDayFromDate(new Date(2020, 11, 21))).toBe("12 - 21");
});

test('new date is ', () => {
    expect(getMonthAndDayFromDate(new Date(2020, 11, 21))).toBe("12 - 21");
});

test('new date from old given a new day of the week', () => {
    const monday = new Date(2020, 11, 21);
    const sundayNextWeek = new Date(2020, 11, 27);
    const wednesday = new Date(2020, 11, 23);
    expect(recalcDateForDayOfWeekFromStartOfPeriod(monday, 1)).toStrictEqual(monday);
    expect(recalcDateForDayOfWeekFromStartOfPeriod(monday, 0)).toStrictEqual(sundayNextWeek);
    expect(recalcDateForDayOfWeekFromStartOfPeriod(monday, 3)).toStrictEqual(wednesday);
});
interface MenuItem {date: Date, id:String, recipe:String};
test('new start date for menuItems', () => {
    const menuItems = JSON.parse(
        '{"startOfPeriod":"2020-12-21T09:00:00.000Z","menuItems":[{"date":"2020-12-27T09:00:00.000Z","id":"2","recipe":"Cannelloni"},{"date":"2020-12-21T09:00:00.000Z","id":"3","recipe":"Witloftaart"},{"date":"2020-12-21T09:00:00.000Z","id":"4","recipe":"Tagliatelle met cashewnoten"},{"date":"2020-12-22T09:00:00.000Z","id":"1","recipe":"Paksoi met mie"}]}'
    ).menuItems;

    menuItems.map(item => {new MenuItem(date=item.date, id=item.id, recipe=item.recipe)})
    const menuStartingWednesday = recalcDates(new Date(2020, 11, 23), menuStartingMonday, new Date(2020, 11, 21));
    console.log('x');
});
