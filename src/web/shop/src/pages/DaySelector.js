import React from "react";
import Select from 'react-select';

const days = [
    { value: '0', label: 'Sun' },
    { value: '1', label: 'Mon' },
    { value: '2', label: 'Tue' },
    { value: '3', label: 'Wed' },
    { value: '4', label: 'Thu' },
    { value: '5', label: 'Fri' },
    { value: '6', label: 'Sat' }
];

export default class DaySelector extends React.Component {

    state = {
        onChange: this.props.onChange
    };

    valueRenderer (option) {
        console.log(`valueRenderer ${option}`);
        return `aap ${option.label} noot`;
        /*
        renderValue: function(option) {
		return <strong style={{ color: option.hex }}>{option.label}</strong>;
	},
         */
    }

    render() {
        return (
            <Select
                defaultValue={days[this.props.selectedOption]}
                name="days"
                options={days}
                value={days[this.props.selectedOption]}
                onChange={this.state.onChange}
                valueRenderer={this.valueRenderer}
            />
        );
    }
}

