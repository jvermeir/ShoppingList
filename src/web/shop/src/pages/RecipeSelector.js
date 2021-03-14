    import React from "react";
import Autocomplete from "react-autocomplete";

export default class RecipeSelector extends React.Component {
    state = {val: this.props.theItem.recipe};

    renderMenuItem(state, val) {
        return (
            state.toLowerCase().indexOf(val.toLowerCase()) !== -1
        );
    }

    render() {
        return (
            <div className="autocomplete-wrapper">
                <Autocomplete
                    key={this.props.theItem.id}
                    value={this.state.val}
                    items={this.props.allRecipes}
                    getItemValue={item => item}
                    shouldItemRender={this.renderMenuItem}
                    renderMenu={item => (
                        <div className="dropdown">
                            {item}
                        </div>
                    )}
                    renderItem={(item, isHighlighted) =>
                        <div key={item} className={`item ${isHighlighted ? 'selected-item' : ''}`}>
                            {item}
                        </div>
                    }
                    onChange={(event, val) =>
                        this.setState({val})
                    }
                    onSelect={val => {
                        this.setState({val});
                        this.props.updateMenu(val, this.props.theItem)
                    }}
                />
            </div>
        )
    }
}
