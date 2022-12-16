import React from 'react';
import './TextArea.css'

const TextArea = ({label, value, ...props}) => {
    return (
        <div className='textArea'>
            <label>{label}</label>
            <textarea className={'input'} value={value} {...props}/>
        </div>
    );
};

export default TextArea;