import React from "react";
import Select from 'react-select';
import {addDaysToDate, getMonthAndDayFromDate} from "../menuFunctions";

const days = [
    { value: '0', label: 'Sun' },
    { value: '1', label: 'Mon' },
    { value: '2', label: 'Tue' },
    { value: '3', label: 'Wed' },
    { value: '4', label: 'Thu' },
    { value: '5', label: 'Fri' },
    { value: '6', label: 'Sat' }
];

export default class DaySelector2 extends React.Component {

    state = {
        onChange: this.props.onChange
    };

    render() {
            console.log("options:");
            console.log(this.props.options);
        return (
            <Select
                defaultValue={this.props.options? this.props.options[this.props.selectedOption]:0}
                value={this.props.options? this.props.options[this.props.selectedOption]:0}
                name="days"
                options={this.props.options}
                onChange={this.state.onChange}
            />
        );
    }
}

