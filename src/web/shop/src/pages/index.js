import React from "react";
import './shop.css';
import DatePicker from './DatePicker.js';
import RecipeSelector from './RecipeSelector.js';
import moment from 'moment';
import DaySelector from "./DaySelector";
import Select from 'react-select';

import {getOptionsForDaySelector, recalcDateForDayOfWeekFromStartOfPeriod, recalcDates} from "../menuFunctions";

const testOpts = [{value: '5', label: 'Sun'},
    {value: '1', label: 'Mon'}
];

export default function Home() {
    return (
        <div>
            <div><App/></div>
            <div><Select
                value={testOpts[0]}
                options={testOpts}
                onChange={e => console.log(e.value)}
            /></div>
        </div>
    )
};

const api = `/api`;

export class App extends React.Component {

    constructor(props) {
        super(props);
        this.saveMenu = this.saveMenu.bind(this);
        this.updateDate = this.updateDate.bind(this);
        this.searchRecipe = this.searchRecipe.bind(this);
        this.state = {
            menuItems: [],
            searchResults: [],
            startOfPeriod: "",
            allRecipes: []
        };
    }

    componentDidMount() {
        this.getMenu();
        this.getAllRecipes();
    }

    dateChanged = (newDate) => {
        this.printContext("dateChanged", this.state.startOfPeriod, this.state.menuItems);
        const newItems = recalcDates(newDate, this.state.menuItems, this.state.startOfPeriod);
        const optionsForDaySelector = getOptionsForDaySelector(newDate);
        this.setState({
            menuItems: newItems, startOfPeriod: newDate,
            optionsForDaySelector: optionsForDaySelector
        }, this.saveMenu);
    }

    render() {
        return (
            <div>
                <div>First day of this menu:
                    <DatePicker date={moment(this.state.startOfPeriod)}
                                onChange={e => this.dateChanged(e.target.value._d)}/>
                </div>
                <div className="top">
                    <div className="table-header">
                        <div className="hidden">id</div>
                        <div>day</div>
                        <div>recipe</div>
                        <div>&#128465;</div>
                    </div>
                    <div className="wrapper">
                        {this.state.menuItems.map((item, index) => {
                            return (
                                <MenuItem key={item.id}
                                          draggable
                                          onDragOver={(e) => e.preventDefault()}
                                          onDrop={(e) => this.handleDrop(e, item.id)}
                                          menuItems={this.state.menuItems}
                                          menuItem={item}
                                          allRecipes={this.state.allRecipes}
                                          startOfPeriod={this.state.startOfPeriod}
                                          updateDateMethod={this.updateDate}
                                          dayOptions={this.state.optionsForDaySelector}
                                          onClick={() => this.deleteMenuItem(item.id, index)}
                                />
                            );
                        })}
                    </div>
                </div>
            </div>
        )
    }

    handleDragStart(e, menuItem) {
        console.log(e);
        console.log(menuItem);
        e.dataTransfer.setData('text/plain', menuItem);
    }

    handleDrop(e, index) {
        console.log(`index: ${index}`);
        console.log(e);
    }

    deleteMenuItem(id, index) {
        this.printContext("deleteMenuItem", this.state.startOfPeriod, this.state.menuItems);
        fetch(`${api}/menu/items/${id}`, {
            method: 'DELETE',
        })
            .then(res => res.text())
            .then(res => console.log(res))
            .then(_ => {
                console.log("calling getMenu from deleteMenuItem")
                this.getMenu()
            })
    }

    searchRecipe(name) {
        fetch(`${api}/recipe/search/${name}`)
            .then(res => res.json())
            .then((data) => this.setState({searchResults: data}))
    }

    getAllRecipes() {
        fetch(`${api}/recipe/names`)
            .then(res => res.json())
            .then((data) => {
                this.setState({allRecipes: data})
            })
            .catch(console.log)
    }

    getMenu() {
        this.printContext("getMenu", this.state.startOfPeriod, this.state.menuItems);
        fetch(`${api}/menu`)
            .then(res => res.json())
            .then((data) => {
                const newMenuItems = this.parseMenuItems(data.menuItems);
                this.setState({menuItems: newMenuItems});
                const newStartOfPeriod = new Date(data.startOfPeriod + "T10:00:00");
                const optionsForDaySelector = getOptionsForDaySelector(newStartOfPeriod);
                this.setState({startOfPeriod: newStartOfPeriod, optionsForDaySelector})
                this.printContext("getMenu - end", newStartOfPeriod, newMenuItems);
            })
            .catch(console.log);
    }

    printContext(tag, startOfPeriod, menuItems) {
        console.log(JSON.stringify({tag, startOfPeriod, menuItems}));
    }

    saveMenu() {
        this.printContext("saveMenu", this.state.startOfPeriod, this.state.menuItems);
        fetch(`${api}/menu`, {
            method: 'POST',
            body: JSON.stringify({startOfPeriod: this.state.startOfPeriod, menuItems: this.state.menuItems}),
            headers: {"Content-type": "application/json"}
        })
            .then(() => {
                console.log("calling getMenu from saveMenu");
                this.getMenu()
            })
            .catch(console.log)
    }

    updateDate(currentItem, daySelected) {
        this.printContext("updateDate", this.state.startOfPeriod, this.state.menuItems);
        const newDate = recalcDateForDayOfWeekFromStartOfPeriod(this.state.startOfPeriod, Number(daySelected));
        console.log(`updating ${currentItem.id} with date ${newDate}`);
        const newItems = this.state.menuItems.map(item => {
            if (item.id === currentItem.id) {
                console.log("item found");
                return {
                    ...item,
                    date: newDate
                }
            } else {
                return item;
            }
        });
        this.setState({menuItems: newItems}, this.saveMenu);
    }

    parseMenuItems(menuItems) {
        return menuItems.map(item => {
            return {
                ...item,
                date: new Date(item.date + "T10:00:00")
            }
        });
    }
}

class MenuItem extends React.Component {

    render() {
        return (
            <>
                <div><SelectADay currentItem={this.props.menuItem}
                                 startOfPeriod={this.props.startOfPeriod}
                                 options={this.props.dayOptions}
                                 updateDateMethod={this.props.updateDateMethod}
                /></div>
                <div><RecipeSelector key={this.props.menuItem.id}
                                     menuItems={this.props.menuItems}
                                     allRecipes={this.props.allRecipes}
                                     theItem={this.props.menuItem}/></div>
                <div>
                    <button onClick={() => this.props.onClick()}>&#128465;</button>
                </div>
            </>
        )
    }
}

class SelectADay extends React.Component {

    render() {
        return (<DaySelector
            dayNumber={this.props.currentItem.date.getDay()}
            startOfPeriod={this.props.startOfPeriod}
            options={this.props.options}
            onChange={e => this.props.updateDateMethod(this.props.currentItem, e.value)}
        />)
    }
}
