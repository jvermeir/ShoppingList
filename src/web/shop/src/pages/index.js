import React, {useState, useEffect} from 'react';
import {Container, Draggable} from 'react-smooth-dnd';
import moment from 'moment';
import './shop.css';

import RecipeSelector from "./RecipeSelector";
import RecipeSelector2 from "./RecipeSelector2";
import {
    applyDrag,
    recalcDates,
    getOptionsForDaySelector,
    getMenu,
    getAllRecipes, generateDays, saveMenu
} from "../menuFunctions";
import DatePicker from "./DatePicker";

// TODO: turn this into typescript and introduce types
export default function Home() {
    return (
        <div><App/></div>
    )
};

function App() {

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

    const updateMenu = (recipeName, menuItem) => {
        const finalRecipeName = recipeName === "" ? "-" : recipeName;
        console.log({text:"updateMenu", finalRecipeName});
        const newMenuItem = {...menuItem, recipe:finalRecipeName};
        const newMenuItems = menu.menuItems.map(m => m.id !== menuItem.id ? m : newMenuItem);
        setMenu({...menu, menuItems:newMenuItems});
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
                                                        menuItems={menu.menuItems}
                                                        allRecipes={recipes}
                                                        theItem={item}
                                                        updateMenu={updateMenu}
                                        />
                                        {/*<RecipeSelector2 key={item.id}*/}
                                        {/*                menuItems={menu.menuItems}*/}
                                        {/*                allRecipes={recipes}*/}
                                        {/*                theItem={item}*/}
                                        {/*                updateMenu={updateMenu}*/}
                                        {/*/>*/}
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

/*
          <div><RecipeSelector key={this.props.menuItem.id}
                                     menuItems={this.props.menuItems}
                                     allRecipes={this.props.allRecipes}
                                     theItem={this.props.menuItem}/></div>
                <div>
 */