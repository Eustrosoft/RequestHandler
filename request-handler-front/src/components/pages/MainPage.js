import React, {useContext, useState} from 'react'
import Header from "../ui/Header/Header";
import Form from "../ui/Form/Form";
import Input from "../ui/Input/Input";
import Counter from "../ui/Counter/Counter";
import TextArea from "../ui/Input/TextArea";
import Select from "../ui/Select/Select";
import Option from "../ui/Select/Option";
import LoginService from "../api/LoginService";
import RequestHandlerService from "../api/RequestHandlerService";
import {AuthContext} from "../context";

function MainPage() {
    const {isAuth, setIsAuth} = useContext(AuthContext);

    const [current, setCurrent] = useState({
        subsystem: 'sql',
        request: 'sql',
        parameters: {
            method: 'plain/text',
            query: ''
        }
    });

    const [query, setQuery] = useState({
        qtisver: 0,
        requests: [{}],
        qtisend: true
    });

    const processRequest = (e) => {
        e.preventDefault();
        if (current.subsystem === 'sql') {
            query.requests.push(current);
            RequestHandlerService.process(query)
                .then(function (response) {
                    if (response.ok) {
                        console.log(response)
                    }
                })
                .catch(err => console.log(err));
        } else if (current.subsystem === 'file') {
            current.parameters = null;
            query.requests.push(current);
        } else {
            console.log('This type of query is not implemented');
        }
    }

    const logout = (e) => {
        e.preventDefault();
        LoginService.logout()
            .then(function (response) {
                if (response.ok) {
                    setIsAuth(false);
                    localStorage.removeItem('auth');
                    window.open("login", "_self");
                }
            })
            .catch(err => console.log(err));
    }

    return (
        <>
            <Header>
                Welcome to Request Dispatcher!
            </Header>
            <Form onSubmit={processRequest}>
                <TextArea label={'Query:'}
                          value={current.query}
                          onChange={e => setCurrent({
                              ...current,
                              parameters: {
                                  ...current.parameters,
                                  query: e.target.value
                              }
                          })}/>
                <Select
                    label={'Type Of Query:'}
                    value={current.subsystem.selectedValue}
                    onChange={e => setCurrent({...current, subsystem: e.target.value})}
                >
                    <Option value={'sql'} label={'SQL query'}/>
                    <Option value={'file'} label={'File Query'}/>
                </Select>
                <Select
                    label={'Type Of Request:'}
                    value={current.request.selectedValue}
                    onChange={e => setCurrent({...current, request: e.target.value})}
                >
                    <Option value={'sql'} label={'SQL'}/>
                    <Option value={'upload'} label={'Upload'}/>
                </Select>
                <Input type={'file'}/>
                <Input type={'submit'}/>
            </Form>
            <button onClick={logout}>
                Logout
            </button>
            <Counter/>
        </>
    );
}

export default MainPage;
