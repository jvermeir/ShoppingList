import React from "react";
import Dropdown from 'react-dropdown'
import './shop.css';
import DatePicker from './DatePicker.js';
import Parent from './experimental.js';
import RecipeSelector from './RecipeSelector.js';
import Gridje from './grid.js';
import moment from 'moment';

export default function Home() {
    return (
        <div>
            <div><App/></div>
            <div><Parent/></div>
            <div><Gridje/></div>
        </div>
    )
}

const api = `/api`;

Date.prototype.addDays = function (days) {
    let date = new Date(this.valueOf());
    date.setDate(date.getDate() + days);
    return date;
}

class App extends React.Component {
    timeout;

    constructor(props) {
        super(props);
        this.saveMenu = this.saveMenu.bind(this);
        this.updateDate = this.updateDate.bind(this);
        this.filterRecipes = this.filterRecipes.bind(this);
        this.searchRecipe = this.searchRecipe.bind(this);
        this.state = {
            menuItems: [],
            theValue: "",
            searchResults: [],
            startOfPeriod: "",
            dummy: "SomeValue",
            allRecipes: []
        };
    }

    componentDidMount() {
        this.getMenu();
        this.getAllRecipes();
    }

    filterRecipes = (value, item) => {
        this.setState({theValue: value});
        const context = this;
        console.log(`value: ${value}`);
        clearTimeout(this.timeout);
        this.timeout = setTimeout(function () {
                console.log(`search value: ${value}`);
                context.searchRecipe(value);
                item.value = context.state.searchResults[0].name;
            },
            1000);
    }

    getNameOfDayFromDate = (newDate) => {
        const dayNames = ["sun", "mon", "tue", "wed", "thu", "fri", "sat"];
        return dayNames[new Date(newDate).getDay()];
    }

    getMonthAndDayFromDate = (newDate) => {
        return `${newDate.getMonth() + 1} - ${newDate.getDate()}`;
    }

    dateChanged = (newDate) => {
        const newStartDay = Math.trunc(newDate / (24 * 60 * 60 * 1000));
        const oldStartDay = Math.trunc(this.state.startOfPeriod / (24 * 60 * 60 * 1000));
        const delta = newStartDay - oldStartDay;
        console.log(`delta: ${delta}`);
        const menuItems = this.state.menuItems;
        const newItems = menuItems.map(item => {
            return {
                ...item,
                date: item.date.addDays(delta)
            };
        });
        this.setState({menuItems: newItems, startOfPeriod: newDate}, this.saveMenu);
        console.log(`value: ${newDate}`);
    }

    render() {
        return (
            <div>
                <div>Start: <DatePicker date={moment(this.state.startOfPeriod)}
                                        onChange={e => this.dateChanged(e.target.value._d)}/></div>
                <div className="top">
                    <div className="table-header">
                        <div className="hidden">id</div>
                        <div>day</div>
                        <div>date</div>
                        <div>recipe</div>
                        <div>&#128465;</div>
                    </div>
                    <div className="wrapper">
                        {this.state.menuItems.map((item, index) => {
                            return (
                                <MenuItem key={item.id} menuItems={this.state.menuItems} menuItem={item}
                                          allRecipes={this.state.allRecipes}
                                          startOfPeriod={this.state.startOfPeriod} parent={this}
                                          updateDateMethod={this.updateDate}
                                          filterRecipes={this.filterRecipes}
                                          onClick={() => this.deleteMenuItem(item.id, index)}
                                />
                            );
                        })}
                    </div>
                </div>
            </div>
        )
    }

    updateMenu(date, index) {
        const items = this.state.menuItems.slice();
        if (index > -1) {
            items.splice(index, 1);
        }
        this.setState({menuItems: items});
    }

    deleteMenuItem(id, index) {
        fetch(`${api}/menu/items/${id}`, {
            method: 'DELETE',
        })
            .then(res => res.text())
            .then(res => console.log(res))
            .then(_ => this.updateMenu(id, index))
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
        fetch(`${api}/menu`)
            .then(res => res.json())
            .then((data) => {
                this.setState({menuItems: this.parseMenuItems(data.menuItems)});
                this.setState({startOfPeriod: new Date(data.startOfPeriod + "T10:00:00")});
            })
            .catch(console.log)
    }

    saveMenu() {
        console.log(JSON.stringify({startOfPeriod: this.state.startOfPeriod, menuItems: this.state.menuItems}));
        fetch(`${api}/menu`, {
            method: 'POST',
            body: JSON.stringify({startOfPeriod: this.state.startOfPeriod, menuItems: this.state.menuItems}),
            headers: {"Content-type": "application/json"}
        })
            .then(res => res.json())
            .then((data) => {
                this.setState({menuItems: this.parseMenuItems(data.menuItems)});
                this.setState({startOfPeriod: new Date(data.startOfPeriod + "T10:00:00")});
            })
            .catch(console.log)
    }

    calculateDate(startOfPeriod, newDay) {
        let newDate = startOfPeriod;
        while (this.getNameOfDayFromDate(newDate) !== newDay) {
            newDate = newDate.addDays(1);
        }
        return newDate;
    }

    updateDate(currentItem, daySelected) {
        const newDate = this.calculateDate(this.state.startOfPeriod, daySelected);
        const newItems = this.state.menuItems.map(item => {
            if (item.id === currentItem.id) {
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
                <div className="hidden">{this.props.parent.getNameOfDayFromDate(this.props.menuItem.date)}</div>
                <div><Days menuItems={this.props.menuItems}
                                             currentItem={this.props.menuItem}
                                             startOfPeriod={this.props.startOfPeriod}
                                             updateDateMethod={this.props.updateDateMethod}
                /></div>
                <div>{this.props.parent.getMonthAndDayFromDate(this.props.menuItem.date)}</div>
                <div><RecipeSelector key={this.props.menuItem.id}
                                                             menuItems={this.props.menuItems}
                                                             allRecipes={this.props.allRecipes}
                                                             theItem={this.props.menuItem}/></div>
                <div><button onClick={() => this.props.onClick()}>&#128465;</button></div>
            </>
        )
    }
}

class Days extends React.Component {
    dayNames = ["sun", "mon", "tue", "wed", "thu", "fri", "sat"];

    render() {
        return (<Dropdown options={this.dayNames}
                          onChange={e => this.props.updateDateMethod(this.props.currentItem, e.value)}
                          value={this.dayNames[this.props.currentItem.date.getDay()]} placeholder="Select an option"/>)
    }
}
