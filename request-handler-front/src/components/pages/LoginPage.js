import React, {useState} from 'react'
import Header from "../ui/Header/Header";
import Form from "../ui/Form/Form";
import Input from "../ui/Input/Input";
import LoginService from "../api/LoginService";

function LoginPage() {
    const [login, setLogin] = useState({login: '', password: ''});

    const loginAction = (e) => {
        e.preventDefault();
        LoginService.login(login)
            .then(function (response) {
                if (response.ok) {
                    window.open("http://localhost:8080/eustrosofthandler_war/api/dispatch", "_self");
                }
            }).catch(err => console.log(err));
    }

    return (
        <>
            <Header>
                Please log in to use Request Dispatcher
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
        </>
    );
}

export default LoginPage;
