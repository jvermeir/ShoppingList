import React from "react";

const host = "http://localhost:8080/api";

class Category extends React.Component {

    constructor(props) {
        super(props);
        this.state = {groente: []};
    }

    componentDidMount() {
        fetch(`${host}/category/groente`, {mode: "cors"})
            .then(res => res.json())
            .then((data) => {
                console.log(data);
                this.setState({groente: data.sequence});
                console.log(this.state.groente);
            })
            .catch(console.log)
    }

    render() {
        return (<div>
            <table>
                <tbody>
                <tr>
                    <td>name</td>
                    <td>value</td>
                </tr>
                <tr>
                    <td>groente</td>
                    <td>{this.state.groente}</td>
                </tr>
                </tbody>
            </table>
            <button onClick={() => this.onGet("sla")}>Get</button>
            <button onClick={() => this.onPut("groente")}>Put</button>
            <button onClick={() => this.onPost("vega")}>Post</button>
            <button onClick={() => this.onDelete("rijst")}>Delete</button>
            <button onClick={() => this.onCategories()}>Categories</button>
        </div>)
    }

    onGet(id) {
        console.log("get");
        fetch(`${host}/category/${id}`, {
            method: 'GET',
        })
            .then(res => res.text())
            .then(res => console.log(res))
    }

    onPut(id) {
        console.log(`put: ${host}/category/${id}`);
        fetch(`${host}/category/${id}`, {
            method: 'PUT',
        })
            .then(res => res.text())
            .then(res => console.log(res))
    }

    onPost(id) {
        console.log("post");
        fetch(`${host}/category/${id}`, {
            method: 'POST',
        })
            .then(res => res.text())
            .then(res => console.log(res))
    }

    onDelete(id) {
        console.log("delete");
        fetch(`${host}/category/${id}`, {
            method: 'DELETE',
        })
            .then(res => res.text())
            .then(res => console.log(res))
    }

    onCategories() {
        console.log("categories");
        fetch(`${host}/categories`, {
            method: 'GET',
        })
            .then(res => res.text())
            .then(res => console.log(res))
    }
}

export default Category;
