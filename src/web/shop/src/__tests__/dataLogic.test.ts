import {getNameOfDayFromDate, getMonthAndDayFromDate, recalcDateForDayOfWeekFromStartOfPeriod, recalcDates} from "../menuFunctions";

describe("test menu functions",  () => {
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

});

describe("test date calculations",  () => {
    class MenuItem {
        date: Date;
        id: String;
        recipe: String;
        constructor(date: Date, id: String, recipe: String) {
            this.date = date;
            this.id = id;
            this.recipe = recipe;
        }
    }

    test('new start date for menuItems if start of period is equal to first date in menu', () => {
        const data = JSON.parse(
            '{"startOfPeriod":"2020-12-21T09:00:00.000Z","menuItems":[{"date":"2020-12-21T09:00:00.000Z","id":"2","recipe":"Cannelloni"},{"date":"2020-12-22T09:00:00.000Z","id":"3","recipe":"Witloftaart"},{"date":"2020-12-23T09:00:00.000Z","id":"4","recipe":"Tagliatelle met cashewnoten"},{"date":"2020-12-24T09:00:00.000Z","id":"1","recipe":"Paksoi met mie"}]}'
        );

        const menuItems = data.menuItems.map(item => {
            return new MenuItem(new Date(item.date), item.id, item.recipe);
        });
        const startOfPeriod = new Date(data.startOfPeriod);

        const menuStartingWednesday = recalcDates(new Date("2020-12-23T09:00:00.000Z"), menuItems, startOfPeriod);
        expect(menuStartingWednesday[0].date).toStrictEqual(new Date("2020-12-23T09:00:00.000Z"));
        expect(menuStartingWednesday[1].date).toStrictEqual(new Date("2020-12-24T09:00:00.000Z"));
        expect(menuStartingWednesday[2].date).toStrictEqual(new Date("2020-12-25T09:00:00.000Z"));
        expect(menuStartingWednesday[3].date).toStrictEqual(new Date("2020-12-26T09:00:00.000Z"));
    });

    test('new start date for menuItems if start of period is not equal to first date in menu', () => {
        const data = JSON.parse(
            '{"startOfPeriod":"2020-12-22T09:00:00.000Z","menuItems":[{"date":"2020-12-24T09:00:00.000Z","id":"2","recipe":"Cannelloni"},{"date":"2020-12-25T09:00:00.000Z","id":"3","recipe":"Witloftaart"},{"date":"2020-12-26T09:00:00.000Z","id":"4","recipe":"Tagliatelle met cashewnoten"},{"date":"2020-12-27T09:00:00.000Z","id":"1","recipe":"Paksoi met mie"}]}'
        );

        const menuItems = data.menuItems.map(item => {
            return new MenuItem(new Date(item.date), item.id, item.recipe);
        });
        const startOfPeriod = new Date(data.startOfPeriod);

        const menuStartingWednesday = recalcDates(new Date("2020-12-23T09:00:00.000Z"), menuItems, startOfPeriod);
        expect(menuStartingWednesday[0].date).toStrictEqual(new Date("2020-12-25T09:00:00.000Z"));
        expect(menuStartingWednesday[1].date).toStrictEqual(new Date("2020-12-26T09:00:00.000Z"));
        expect(menuStartingWednesday[2].date).toStrictEqual(new Date("2020-12-27T09:00:00.000Z"));
        expect(menuStartingWednesday[3].date).toStrictEqual(new Date("2020-12-28T09:00:00.000Z"));
    });

    test('new start date for menuItems if new start of period is after last day in menu', () => {
        const data = JSON.parse(
            '{"startOfPeriod":"2020-12-22T09:00:00.000Z","menuItems":[{"date":"2020-12-24T09:00:00.000Z","id":"2","recipe":"Cannelloni"},{"date":"2020-12-25T09:00:00.000Z","id":"3","recipe":"Witloftaart"},{"date":"2020-12-26T09:00:00.000Z","id":"4","recipe":"Tagliatelle met cashewnoten"},{"date":"2020-12-27T09:00:00.000Z","id":"1","recipe":"Paksoi met mie"}]}'
        );

        const menuItems = data.menuItems.map(item => {
            return new MenuItem(new Date(item.date), item.id, item.recipe);
        });
        const startOfPeriod = new Date(data.startOfPeriod);

        const menuStartingWednesday = recalcDates(new Date("2021-01-01T09:00:00.000Z"), menuItems, startOfPeriod);
        expect(menuStartingWednesday[0].date).toStrictEqual(new Date("2021-01-03T09:00:00.000Z"));
        expect(menuStartingWednesday[1].date).toStrictEqual(new Date("2021-01-04T09:00:00.000Z"));
        expect(menuStartingWednesday[2].date).toStrictEqual(new Date("2021-01-05T09:00:00.000Z"));
        expect(menuStartingWednesday[3].date).toStrictEqual(new Date("2021-01-06T09:00:00.000Z"));
    });
});
