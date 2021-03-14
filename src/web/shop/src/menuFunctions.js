const api = `/api`;

const addDaysToDate = (date, days) => {
    const newDate = new Date(date);
    return new Date(newDate.setDate(date.getDate() + days));
}

const getNameOfDayFromDate = (newDate) => {
    const dayNames = ["sun", "mon", "tue", "wed", "thu", "fri", "sat"];
    return dayNames[new Date(newDate).getDay()];
}

const getOptionTextFromDate = (newDate) => {
    return {
        day: getNameOfDayFromDate(newDate)
        , month: newDate.getMonth() + 1
        , date: newDate.getDate()
    }
}

const recalcDates = (newStartOfPeriod, menuItems, startOfPeriod) => {
    const newStartDay = Math.trunc(newStartOfPeriod / (24 * 60 * 60 * 1000));
    const oldStartDay = Math.trunc(startOfPeriod / (24 * 60 * 60 * 1000));
    const delta = newStartDay - oldStartDay;
    const newMenuItems = menuItems.map(item => {
        return {
            ...item,
            date: addDaysToDate(item.date, delta)
        };
    });
    return newMenuItems;
}

const recalcDateForDayOfWeekFromStartOfPeriod = (startOfPeriod, newDay) => {
    const dayOfStartOfPeriod = startOfPeriod.getDay();
    let delta = newDay - dayOfStartOfPeriod;
    if (delta < 0) delta += 7;
    return addDaysToDate(startOfPeriod, delta);
}

const getOptionsForDaySelector = (startOfPeriod) => {
    let days = [];
    let date = startOfPeriod;
    for (let i = 0; i < 7; i++) {
        days.push({value: date.getDay(), label: getOptionTextFromDate(date)})
        date = addDaysToDate(date, 1);
    }
    return days;
}

function getAllRecipes(setRecipes) {
    fetch(`${api}/recipe/names`)
        .then(res => res.json())
        .then((data) => {
            setRecipes (data)
        })
        .catch(console.log)
}

const parseMenuItems = (menuItems) => {
    return menuItems.map(item => {
        return {
            ...item,
            date: new Date(item.date + "T10:00:00")
        }
    });
}

function getMenu(setMenu) {
    console.log('getMenu - start');
    fetch(`${api}/menu`)
        .then(res => res.json())
        .then((data) => {
                const menuItems = parseMenuItems(data.menuItems);
                const startOfPeriod = new Date(data.startOfPeriod + "T10:00:00");
                const menu = {menuItems, startOfPeriod};
                console.log({menu});
                setMenu(menu);
            }
        );
    console.log('getMenu - end');
}

function saveMenu(menu) {
    console.log('save menu -  start');
    fetch(`${api}/menu`, {
        method: 'POST',
        body: JSON.stringify({startOfPeriod: menu.startOfPeriod, menuItems: menu.menuItems}),
        headers: {"Content-type": "application/json"}
    }).catch(console.log)
    console.log('save menu - end');
}

const generateDays = (dateOfFirstDay) => {
    console.log(`${dateOfFirstDay}`);
    const result = [];
    for (let i = 0; i < 7; i++) {
        result.push(getOptionTextFromDate(addDaysToDate(dateOfFirstDay, i)));
    }
    return result;
};

const applyDrag = (arr, dragResult, startOfPeriod) => {
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

const generateItems = (count, creator) => {
    const result = [];
    for (let i = 0; i < count; i++) {
        result.push(creator(i));
    }
    return result;
};

export {
    recalcDates, addDaysToDate, getNameOfDayFromDate, getOptionsForDaySelector, getOptionTextFromDate,
    recalcDateForDayOfWeekFromStartOfPeriod, getAllRecipes, getMenu, saveMenu, generateDays, updateDate, generateItems, applyDrag
};