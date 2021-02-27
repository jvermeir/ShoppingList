import React, {useState, useEffect} from 'react';
import {Container, Draggable} from 'react-smooth-dnd';
import moment from 'moment';

import RecipeSelector from "../pages/RecipeSelector";
import {
    applyDrag,
    recalcDates,
    getOptionsForDaySelector,
    getMenu,
    getAllRecipes, generateDays, saveMenu
} from "../menuFunctions";
import DatePicker from "../pages/DatePicker";

export function Test() {

    const [recipes, setRecipes] = useState();
    const [menu, setMenu] = useState();
    const [days, setDays] = useState();

    useEffect(() => {
        getAllRecipes(setRecipes);
        getMenu(setMenu);
    }, []);

    useEffect(() => {
        if (menu) {
            saveMenu(menu);
            setDays(generateDays(menu.startOfPeriod));
        }
    }, [menu])

    const dateChanged = (newDate) => {
        const newItems = recalcDates(newDate, menu.menuItems, menu.startOfPeriod);
        setDays(getOptionsForDaySelector(newDate));
        setMenu({menuItems: newItems, startOfPeriod: newDate});
    }

    // TODO: add a remove option that clears the menu for a day
    // TODO: update recipe when selecting from drop down
    return (
        <div>
            {menu &&
            <div>First day of this menu:
                <DatePicker date={moment(menu.startOfPeriod)}
                            onChange={e => dateChanged(e.target.value._d)}/>
            </div>}
            {
                menu && recipes &&
                <div style={{display: 'flex', flexDirection: 'row', width: '100%'}}>
                    <div style={{flex: 10}}>
                        {
                            days && days.map(day => {
                                return (
                                    <div style={{display: 'flex', alignItems: 'center', height: 42}}
                                         key={day.date}
                                    >
                                        <span>{day.month} {day.date}</span>
                                    </div>
                                );
                            })
                        }
                    </div>
                    <Container groupName="1" getChildPayload={i => menu.menuItems[i]}
                               onDrop={e => {
                                   const menuItems = applyDrag(menu.menuItems, e, menu.startOfPeriod);
                                   const newMenu = {...menu, menuItems};
                                   console.log({newMenu});
                                   setMenu(newMenu);
                               }
                               }
                               style={{flex: 100, marginLeft: '5px'}}
                    >
                        {
                            menu.menuItems.map(item => {
                                return (
                                    <Draggable key={item.id}
                                               style={{display: 'flex', flexDirection: 'row'}}>
                                        <img src="drag.png" style={{display: 'block', height: 40, width: 'auto'}} alt="drag"/>
                                        <RecipeSelector key={item.id}
                                                        allRecipes={recipes}
                                                        theMenuItem={item}/>
                                    </Draggable>
                                );
                            })
                        }
                    </Container>
                </div>
            }
        </div>
    );
}

export default Test;
