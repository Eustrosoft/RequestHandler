import React, {useEffect, useState} from 'react'
import './styles/App.css';
import MainPage from "./components/pages/MainPage";
import LoginPage from "./components/pages/LoginPage";
import {AuthContext} from "./components/context";

function App() {
    const [isAuth, setIsAuth] = useState(false);

    useEffect(() => {
        if (localStorage.getItem('auth')) {
            setIsAuth(true);
        }
    }, [])

    const loginPage = () => {
        if (window.location.pathname === "/" ||
            window.location.pathname === "/login") {
            return <LoginPage/>
        }
    }

    const mainPage = () => {
        if (window.location.pathname === "/main") {
            return <MainPage/>
        }
    }

    return (
        <AuthContext.Provider value={{
            isAuth,
            setIsAuth
        }}>
            <div className='main'>
                {loginPage()}
                {mainPage()}
            </div>
        </AuthContext.Provider>
    );
}

export default App;
