import React from 'react';
import './Form.css'

const Form = ({children, ...props}) => {
    return (
        <form className={'sendForm'} {...props}>
            {children}
        </form>
    );
};

export default Form;