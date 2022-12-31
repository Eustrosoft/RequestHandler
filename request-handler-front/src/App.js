import React from 'react'
import './styles/App.css';
import MainPage from "./components/pages/MainPage";
import LoginPage from "./components/pages/LoginPage";

function App() {

    const loginPage = () => {
        if (window.location.pathname === "/main/" ||
            window.location.pathname === "/" ||
            window.location.pathname === "/main/index.html" ||
            window.location.pathname === "/login") {
            return <LoginPage/>
        }
    }

    const mainPage = () => {
        if (window.location.pathname === "/main/main") {
            return <MainPage/>
        }
    }

    return (
        <div className='main'>
            {loginPage()}
            {mainPage()}
        </div>
    );
}

export default App;
