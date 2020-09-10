import React from "react";

export default function Home() {
    return (
        <div><div><App/></div></div>
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
        return <div>menu
            <table>
                <thead><tr><th>date</th><th>day</th><th>recipe</th><th> </th></tr></thead>
                <tbody>
                {this.state.menuItems.map((item, index) => {
                    return (
                            <MenuItem key={item.date} menuItem={item}
                                      onClick={() => this.handleClick(item.date, index)}
                            />
                    );
                })}
                </tbody>
            </table>
        </div>
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
                <tr>
                <td>{this.props.menuItem.date}</td>
                <td>{this.props.menuItem.dayOfWeek}</td>
                <td>{this.props.menuItem.recipe}</td>
                <td><button onClick={() => this.props.onClick()}>delete</button></td>
                </tr>
        )
    }
}
