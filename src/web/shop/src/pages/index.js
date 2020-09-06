import React from "react";
import Groente from "./category";

export default function Home() {
    return (
        <div>
            <div>This weeks menu:</div>
            <div>groente category: <Groente/></div>
            <div><App/></div>
        </div>
    )
}

const host = "http://localhost:8080/api";

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
            <div>
            <MenuItemsList menuItems={this.state.menuItems}/>
            </div>
        )
    }

    getMenu() {
        fetch(`${host}/menu`)
            .then(res => res.json())
            .then((data) => {
                this.setState({menuItems: data.menuItems})
            })
            .catch(console.log)
    }
}

class MenuItemsList extends React.Component {
    render() {
        const menuItems = this.props.menuItems.map(menuItem =>
            <MenuItem key={menuItem.date} menuItem={menuItem}/>
        );
        return (
            <table>
                <tbody>
                <tr>
                    <th>date</th>
                    <th>dayOfWeek</th>
                    <th>recipe</th>
                </tr>
                {menuItems}
                </tbody>
            </table>
        )
    }
}

class MenuItem extends React.Component {
    onDelete(id) {
        console.log("delete");
        fetch(`${host}/menu/items/${id}`, {
            method: 'DELETE',
        })
            .then(res => res.text())
            .then(res => console.log(res))
    }

    render() {
        return (
            <tr>
                <td>{this.props.menuItem.date}</td>
                <td>{this.props.menuItem.dayOfWeek}</td>
                <td>{this.props.menuItem.recipe}</td>
                <td>
                    <button onClick={() => this.onDelete(this.props.menuItem.date)}>Delete</button>
                </td>
            </tr>
        )
    }
}