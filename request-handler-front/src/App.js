import React, {useState} from 'react'
import './styles/App.css';
import Form from "./components/ui/Form/Form";
import Input from "./components/ui/Input/Input";
import Header from "./components/ui/Header/Header";

function App() {
    const [login, setLogin] = useState({login: '', password: ''});

    const loginAction = (e) => {
        e.preventDefault();
        console.log("Hello!");
        console.log(JSON.stringify(login))
        fetch('http://localhost:8080/eustrosofthandler_war/login', {
            method: 'POST',
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json',
                'Access-Control-Allow-Origin': '*',
                'Access-Control-Allow-Methods': 'GET, POST, PATCH, PUT, DELETE, OPTIONS',
                'Access-Control-Allow-Headers': 'Origin, Content-Type, X-Auth-Token'
            },
            body: JSON.stringify(login)
        })
            .then(function (response) {
                return response.json();
            })
            .then(function (data) {
                console.log(data)
            }).catch(err => console.log(err));
    }

    return (
        <div className='main'>
            <Header>
                Welcome to Request Dispatcher
            </Header>
            <Form onSubmit={loginAction}>
                <Input
                    label={'Login:'}
                    type={'text'}
                    onChange={e => setLogin({...login, login: e.target.value})}
                    placeholder={'Login'}
                />
                <Input
                    label={'Password:'}
                    type={'password'}
                    onChange={e => setLogin({...login, password: e.target.value})}
                    placeholder={'Password'}
                />
                <Input type={'submit'}/>
            </Form>
        </div>
    );
}

export default App;
