import React from "react";
import Select from 'react-select';

export default class DaySelector extends React.Component {

    state = {
        onChange: this.props.onChange
    };

    getOptionIndexFromDay() {
        if (this.props.options === undefined) {
            return 0;
        }
        let i = 0;
        while (i < this.props.options.length && this.props.options[i].value !== this.props.dayNumber) i++;
        return this.props.options[i];
    }

    // TODO: this seems more complicated than necessary...
    render() {
        return (
            <Select
                defaultValue={this.getOptionIndexFromDay()}
                value={this.getOptionIndexFromDay()}
                name="days"
                options={this.props.options}
                onChange={this.state.onChange}
            />
        );
    }
}

