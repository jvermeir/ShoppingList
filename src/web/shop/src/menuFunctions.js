function x() {
    return 2;
}

const addDaysToDate = (date, days) => {
    const newDate = new Date(date);
    return new Date(newDate.setDate(date.getDate() + days));
}

const getNameOfDayFromDate = (newDate) => {
    const dayNames = ["sun", "mon", "tue", "wed", "thu", "fri", "sat"];
    return dayNames[new Date(newDate).getDay()];
}

const getMonthAndDayFromDate = (newDate) => {
    return `${newDate.getMonth() + 1} - ${newDate.getDate()}`;
}

const recalcDates = (newStartOfPeriod, menuItems, startOfPeriod) => {
    const newStartDay = Math.trunc(newStartOfPeriod / (24 * 60 * 60 * 1000));
    const oldStartDay = Math.trunc(startOfPeriod / (24 * 60 * 60 * 1000));
    const delta = newStartDay - oldStartDay;
    const newMenuItems = menuItems.map(item => {
        return {
            ...item,
            date: addDaysToDate(item.date,delta)
        };
    });
    return newMenuItems;
}

const recalcDateFromStartOfPeriod = (startOfPeriod, newDay) => {let newDate = startOfPeriod;
    const dayOfStartOfPeriod = startOfPeriod.getDay();
    let delta = newDay - dayOfStartOfPeriod;
    if (delta<0) delta += 7;
    return addDaysToDate(startOfPeriod, delta);
}

export {
    recalcDates, x, addDaysToDate, getNameOfDayFromDate,
    getMonthAndDayFromDate, recalcDateFromStartOfPeriod
};