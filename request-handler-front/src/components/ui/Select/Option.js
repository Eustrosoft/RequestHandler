import React from 'react';

const Option = ({value, label}) => {
    return (
        <option value={value}>
            {label}
        </option>
    );
};

export default Option;