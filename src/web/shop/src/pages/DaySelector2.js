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
            console.log(`options: ${this.props.options}`);
        return (
            <Select
                defaultValue={days[this.props.selectedOption]}
                name="days"
                options={this.props.options}
                value={this.props.selectedOption}
                // value={days[this.props.selectedOption]}
                onChange={this.state.onChange}
            />
        );
    }
}

