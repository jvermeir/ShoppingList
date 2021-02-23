// TODO: moving a recipe should change its date

import {addDaysToDate} from "../menuFunctions";

export const applyDrag = (arr, dragResult, startOfPeriod) => {
    const { removedIndex, addedIndex, payload } = dragResult;
    if (removedIndex === null && addedIndex === null) return arr;

    const result = [...arr];
    let itemToAdd = payload;

    if (removedIndex !== null) {
        itemToAdd = result.splice(removedIndex, 1)[0];
    }

    if (addedIndex !== null) {
        result.splice(addedIndex, 0, itemToAdd);
    }
    let i = 0;
    const newData = result.map(item => updateDate(item, i++, startOfPeriod));
    return newData;
};

const updateDate = (menuItem, i, startOfPeriod) => {
    return { ...menuItem, date: addDaysToDate(startOfPeriod, i)};
}

export const generateItems = (count, creator) => {
    const result = [];
    for (let i = 0; i < count; i++) {
        result.push(creator(i));
    }
    return result;
};