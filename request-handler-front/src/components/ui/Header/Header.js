import React from 'react';
import './Header.css'

const Header = ({children, ...props}) => {
    return (
        <h1 className={'header'} {...props}>
            {children}
        </h1>
    );
};

export default Header;