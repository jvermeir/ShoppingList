import React from "react";
import './shop.css';
import DatePicker from './DatePicker.js';

export default function Home() {
    return (
        <div>
            <div><App/></div>
        </div>
    )
}

const api = `http://${window.location.hostname}:8080/api`;

Date.prototype.addDays = function (days) {
    let date = new Date(this.valueOf());
    date.setDate(date.getDate() + days);
    return date;
}

class App extends React.Component {
    timeout;

    constructor(props) {
        super(props);
        this.state = {menuItems: [], theValue: "", searchResults: [], startOfPeriod: ""};
    }

    componentDidMount() {
        this.getMenu()
    }

    filterRecipes = (value, context) => {
        this.setState({theValue: value});
        clearTimeout(this.timeout);
        this.timeout = setTimeout(function () {
                context.searchRecipe(value);
            },
            1000);
    };

    getNameOfDayFromDate = (newDate) => {
        const dayNames = ["sun", "mon", "tue", "wed", "thu", "fri", "sat"];
        return dayNames[new Date(newDate).getDay()];
    }

    getMonthAndDayFromDate = (newDate) => {
        return `${newDate.getMonth()+1} - ${newDate.getDate()}`;
    }

    dateChanged = (newDate, context) => {
        let theDay = new Date(newDate);
        const menuItems = this.state.menuItems;
        const newItems = menuItems.map(item => {
            const currentDay = theDay;
            theDay = theDay.addDays(1)
            return {
                ...item,
                dayOfWeek: this.getNameOfDayFromDate(currentDay),
                date: currentDay
            };
        });
        this.setState({menuItems: newItems, startOfPeriod: newDate}, this.saveMenu);
        console.log(`value: ${newDate}`);
    }

    render() {
        return (
            <div>
                <DatePicker onChange={e => this.dateChanged(e.target.value, this)}/>
                <form>
                <label>Menu for week starting on date (yyyymmdd)
                    <input type="text" onBlur={e => this.dateChanged(e.target.value, this)}/>
            </label></form>
                <div className="top">
                    <div className="grid-container">
                        <div className="hidden">id</div>
                        <div className="grid-item">date</div>
                        <div className="grid-item">day</div>
                        <div className="grid-item">recipe</div>
                        <div className="grid-item">action</div>
                        {this.state.menuItems.map((item, index) => {
                            return (
                                <MenuItem key={item.id} menuItem={item} parent={this}
                                          onClick={() => this.deleteMenuItem(item.id, index)}
                                />
                            );
                        })}
                    </div>
                </div>
                <form>
                    <label>
                        Search:
                        <input type="text" onChange={e => this.filterRecipes(e.target.value, this)}/>
                    </label>
                </form>
                <p>Search Value: {this.state.theValue}</p>
                <div>{this.state.searchResults.map((recipe) => {
                    return (
                        <Recipe key={recipe.name} recipe={recipe}/>
                    )
                })}
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

    getMenu() {
        fetch(`${api}/menu`)
            .then(res => res.json())
            .then((data) => {
                this.setState({menuItems: this.parseMenuItems(data.menuItems)})
                this.setState({startOfPeriod: data.startOfPeriod})
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
            .then(res => res.text())
            .then(res => console.log(res))
    }

    parseMenuItems(menuItems) {
        return menuItems.map(item => { return {
        ...item,
        date: new Date(item.date)
        }
        });
    }
}

class MenuItem extends React.Component {
    render() {
        return (
            <React.Fragment>
                <div className="hidden">{this.props.menuItem.id}</div>
                <div className="hidden">{this.props.menuItem.date.toJSON()}</div>
                <div className="grid-item">{this.props.parent.getNameOfDayFromDate(this.props.menuItem.date)}</div>
                <div className="grid-item">{this.props.parent.getMonthAndDayFromDate(this.props.menuItem.date)}</div>
                <div className="grid-item">{this.props.menuItem.recipe}</div>
                <div className="grid-item">
                    <button onClick={() => this.props.onClick()}>delete</button>
                </div>
            </React.Fragment>
        )
    }
}

class Recipe extends React.Component {
    render() {
        return (
            <React.Fragment>
                <div>{this.props.recipe.name}</div>
            </React.Fragment>
        )
    }
}