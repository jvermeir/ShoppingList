import React from "react";
import './shop.css';

export default function Home() {
    return (
        <div>
            <div><App/></div>
        </div>
    )
}

const api = "http://localhost:8080/api";

class App extends React.Component {
    timeout;

    constructor(props) {
        super(props);
        this.state = {menuItems: [], theValue: "", searchResults: [], day: ""};
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

    _onBlur = (value, context) => {
        this.setState({day: value});
        console.log(`value: ${value}`);
    }

    firstDay (day) {
        var today = new Date();
        
    }

    render() {
        return (
            <div><form>
                <label>Menu for week starting on
                    <input type="text" onBlur={e => this._onBlur(e.target.value, this)}/>
            </label></form>
                <div className="top">
                    <div className="grid-container">
                        <div className="grid-item">date</div>
                        <div className="grid-item">day</div>
                        <div className="grid-item">recipe</div>
                        <div className="grid-item">action</div>
                        {this.state.menuItems.map((item, index) => {
                            return (
                                <MenuItem key={item.date} menuItem={item}
                                          onClick={() => this.deleteMenuItem(item.date, index)}
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

    deleteMenuItem(date, index) {
        fetch(`${api}/menu/items/${date}`, {
            method: 'DELETE',
        })
            .then(res => res.text())
            .then(res => console.log(res))
            .then(_ => this.updateMenu(date, index))
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
                this.setState({menuItems: data.menuItems})
            })
            .catch(console.log)
    }
}

class MenuItem extends React.Component {
    render() {
        return (
            <React.Fragment>
                <div className="grid-item">{this.props.menuItem.date}</div>
                <div className="grid-item">{this.props.menuItem.dayOfWeek}</div>
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