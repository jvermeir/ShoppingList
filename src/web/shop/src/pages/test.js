import React, {useState, useEffect} from 'react';
import {Container, Draggable} from 'react-smooth-dnd';
import {applyDrag} from './utils';
import moment from 'moment';

import RecipeSelector from "./RecipeSelector";
import {
    recalcDates,
    getOptionsForDaySelector,
    getMenu,
    getAllRecipes, generateDays, saveMenu
} from "../menuFunctions";
import DatePicker from "./DatePicker";

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
        // todo: this will become data for the day column
        const optionsForDaySelector = getOptionsForDaySelector(newDate);
        setMenu({menuItems: newItems, startOfPeriod: newDate});
    }

    // TODO: add first column with dates
    // TODO: add some space at the start of each recipe to use as a handle
    // TODO: add a remove option that clears the menu for a day
    return (
        <div>
            {menu &&
            <div>First day of this menu:
                <DatePicker date={moment(menu.startOfPeriod)}
                            onChange={e => dateChanged(e.target.value._d)}/>
            </div>}
            {
                menu && recipes &&
                <div>
                    <Container groupName="1" getChildPayload={i => menu.menuItems[i]}
                               onDrop={e => {
                                   const menuItems = applyDrag(menu.menuItems, e, menu.startOfPeriod);
                                   const newMenu = {...menu, menuItems};
                                   console.log({newMenu});
                                   setMenu(newMenu);
                               }
                               }
                    >
                        {
                            menu.menuItems.map(item => {
                                return (
                                    <Draggable key={item.id}>
                                        <div style={{flex: 1, flexDirection: 'row'}}>
                                            <div>handle</div>
                                            <RecipeSelector key={item.id}
                                                            menuItems={menu.menuItems}
                                                            allRecipes={recipes}
                                                            theItem={item}/>
                                        </div>
                                    </Draggable>
                                );
                            })
                            }
                            </Container>
                            </div>}
                </div>
                );
            }

export default Test;
