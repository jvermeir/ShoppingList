import React from "react";
import './shop.css';
import DatePicker from './DatePicker.js';
import Parent from './experimental.js';
import RecipeSelector from './RecipeSelector.js';
import Gridje from './grid.js';
import moment from 'moment';
import DaySelector from "./select";
import { recalcDates, addDaysToDate, getNameOfDayFromDate, getMonthAndDayFromDate, recalcDateFromStartOfPeriod } from "../menuFunctions";

export default function Home() {
    return (
        <div>
            <div><App/></div>
            <div><Parent/></div>
            <div><Gridje/></div>
            <div><DaySelector selectedOption="0"/></div>
        </div>
    )
}

const api = `/api`;

export class App extends React.Component {
    timeout;

    constructor(props) {
        super(props);
        this.saveMenu = this.saveMenu.bind(this);
        this.updateDate = this.updateDate.bind(this);
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

    dateChanged = (newDate) => {
        const newItems = recalcDates(newDate, this.state.menuItems, this.state.startOfPeriod);
        this.setState({menuItems: newItems, startOfPeriod: newDate}, this.saveMenu);
        console.log(`value: ${newDate}`);
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

    updateDate(currentItem, daySelected) {
        const newDate = recalcDateFromStartOfPeriod(this.state.startOfPeriod,  Number(daySelected));
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
                <div className="hidden">{getNameOfDayFromDate(this.props.menuItem.date)}</div>
                <div><SelectADay currentItem={this.props.menuItem}
                                 updateDateMethod={this.props.updateDateMethod}
                /></div>
                <div>{getMonthAndDayFromDate(this.props.menuItem.date)}</div>
                <div><RecipeSelector key={this.props.menuItem.id}
                                     menuItems={this.props.menuItems}
                                     allRecipes={this.props.allRecipes}
                                     theItem={this.props.menuItem}/></div>
                <div><button onClick={() => this.props.onClick()}>&#128465;</button></div>
            </>
        )
    }
}

class SelectADay extends React.Component {
    render() {
        return (<DaySelector selectedOption={this.props.currentItem.date.getDay()}
                             onChange={e => this.props.updateDateMethod(this.props.currentItem, e.value)}
        />)
    }
}
