import React from "react";

export default class Parent extends React.Component {
    constructor(props) {
        super(props)

        this.handler = this.handler.bind(this)
    }

    loggie() {
        console.log(`test: ${this.state.someVar}`);
    }

    handler(vallie) {
        this.setState({
            someVar: vallie
        }, this.loggie)
    }

    render() {
        return <Child handler = {this.handler} />
    }
}

class Child extends React.Component {
    render() {
        return <>childStartsHere
            <div><GrandChild handler={this.props.handler} /></div>
            childEndsHere</>
    }
}

class GrandChild extends React.Component {
    render() {
        return <>grandChildStartsHere
        <div><button onClick = {() => this.props.handler('text')}>click</button></div>
            grandChildEndsHere</>
    }
}

