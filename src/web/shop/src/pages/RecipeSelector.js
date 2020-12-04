import React from "react";
import Autocomplete from "react-autocomplete";

export default class RecipeSelector extends React.Component {
    state = { val: this.props.theItem.recipe };

    renderMenuItem(state, val) {
        return (
            state.recipe.toLowerCase().indexOf(val.toLowerCase()) !== -1
        );
    }

    render() {
        return (
            <div className="autocomplete-wrapper">
                <Autocomplete
                    key={this.props.theItem.id}
                    value={this.state.val}
                    items={this.props.menuItems}
                    getItemValue={item => item.recipe}
                    shouldItemRender={this.renderMenuItem}
                    renderMenu={item => (
                        <div className="dropdown">
                            {item}
                        </div>
                    )}
                    renderItem={(item, isHighlighted) =>
                        <div className={`item ${isHighlighted ? 'selected-item' : ''}`}>
                            {item.recipe}
                        </div>
                    }
                    onChange={(event, val) => {
                        this.setState({val});
                        console.log(val);
                    }}
                    onSelect={val => this.setState({ val })}
                />
            </div>
        );
    }
}
