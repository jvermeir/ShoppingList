import React from "react";
import Selectors from "./SingleSelect";

// class App extends React.Component {
//     constructor(props) {
//         super(props);
//         this.superheroElement = React.createRef();
//     }
//     handleClick = () => {
//         this.superheroElement.current.changeName();
//     };
//     render() {
//         return (
//             <div className="App">
//                 <Superhero ref={this.superheroElement} />
//                 <button onClick={this.handleClick}>Show real name</button>
//             </div>
//         );
//     }
// }

export default class LinkedSelectors extends React.Component {

    constructor(props) {
        super(props);
        this.selectorBElement = React.createRef();
        this.copyToB = this.copyToB.bind(this);
    }

    copyToB(value) {
        console.log(`parent - copyToB: ${value}`);
        this.selectorBElement.current.setValue(value);
    }

    render() {
        return (
            <div>
                <div><SelectorA copyToBFunction={this.copyToB}/></div>
                <div><SelectorB ref={this.selectorBElement} selectedOption="4"/></div>
            </div>
        )
    }
}

class SelectorA extends React.Component {
    copyToB = (value) => {
        console.log(`selectorA - copyToB: ${value}`);
        this.setState({"selectedValue": value});
        this.props.copyToBFunction(value);
    }

    render() {
        console.log(`render SelectorA`);
        return (<Selectors selectedOption="3"
                           onChange={(e) => this.copyToB(e.value)}
            />
        )
    }
}

class SelectorB extends React.Component {
    state = {selectedOption:this.props.selectedOption}

    constructor(props) {
        super(props);
        this.it = React.createRef();
        this.setValue = this.setValue.bind(this);
    }

    setValue = (value) => {
        console.log(`selectorb - setValue ${value}`);
        this.it.selectValue=value;
        this.setState({selectedOption: value})
    }

    render() {
        console.log(`render SelectorB`);
        return (<Selectors ref={this.it}
                           defaultValue="1"
                           // value={this.state.selectedOption}
                           selectValue={this.state.selectedOption}

            />
        )
    }
}
