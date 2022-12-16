import React from 'react';
import './Select.css'

const Select = ({label, children, ...props}) => {
    return (
        <>
            <label>{label}</label>
            <select className={'select'} {...props}>
                {children}
            </select>
        </>
    );
};

export default Select;