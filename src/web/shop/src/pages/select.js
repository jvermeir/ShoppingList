import React from "react";
import Select from 'react-select';

const days = [
    { value: '0', label: 'Sun' },
    { value: '1', label: 'Mon' },
    { value: '2', label: 'Tue' },
    { value: '3', label: 'Wed' },
    { value: '4', label: 'Thu' },
    { value: '5', label: 'Fri' },
    { value: '6', label: 'Sat' },
];

export default class DaySelector extends React.Component {

    state = {
        selectedOption: this.props.selectedOption,
        onChange: this.props.onChange
    };

    render() {
        const { selectedOption, onChange } = this.state;

        return (
            <Select
                defaultValue={days[selectedOption]}
                name="days"
                options={days}
                onChange={onChange}
            />
        );
    }
}
