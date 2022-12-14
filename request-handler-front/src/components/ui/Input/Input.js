import React from 'react';
import './Input.css'

const Input = ({type, label, ...props}) => {
    return (
        <>
            <label>{label}</label>
            <input className={'input'} {...props} type={type}/>
        </>
    );
};

export default Input;