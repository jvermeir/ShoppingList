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
    constructor(props) {
        super(props);
        this.state = {menuItems: []};
    }

    componentDidMount() {
        this.getMenu()
    }

    render() {
        return (
            <div>Menu
                <div className="top">
                    <div className="grid-container">
                        <div className="grid-item">date</div>
                        <div className="grid-item">day</div>
                        <div className="grid-item">recipe</div>
                        <div className="grid-item">action</div>
                        {this.state.menuItems.map((item, index) => {
                            return (
                                <MenuItem key={item.date} menuItem={item}
                                          onClick={() => this.handleClick(item.date, index)}
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

    handleClick(date, index) {
        fetch(`${api}/menu/items/${date}`, {
            method: 'DELETE',
        })
            .then(res => res.text())
            .then(res => console.log(res))
            .then(_ => this.updateMenu(date, index))
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
