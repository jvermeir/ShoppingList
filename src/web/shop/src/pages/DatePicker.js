import React, { useState } from "react";
import { SingleDatePicker } from "react-dates";
import "react-dates/initialize";
import "react-dates/lib/css/_datepicker.css";

export default function DatePicker({ date, onChange }) {
    const [focused, setFocused] = useState(false);

    return (
        <SingleDatePicker
            date={date}
            selected={date}
            onDateChange={date => onChange({ target: { value: date } })}
            focused={focused}
            onFocusChange={({ focused }) => setFocused(focused)}
        />
    );
}
