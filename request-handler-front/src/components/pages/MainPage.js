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

    const [response, setResponse] = useState('');

    const [data, setData] = useState({
        file: {},
        name: "",
        ext: ""
    });

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
        requests: [],
        qtisend: true
    });

    const processRequest = (e) => {
        e.preventDefault();
        if (current.subsystem === 'sql') {
            query.requests = [current];
        } else if (current.subsystem === 'file') {
            current.parameters = {
                method: 'application/octet-stream',
                data: data
            };
            query.requests = [current];
        } else {
            console.log('This type of query is not implemented');
        }
        RequestHandlerService.process(query)
            .then(resp => resp.json())
            .then(json => setResponse(JSON.stringify(json)))
            .catch(err => console.log(err));
    }
    /*
        const parseArray = json => {
            return json === '[]' ? [] : parseElements(json) ;
        };

        const parseElements = json => {
            const elementLocations = [0];
            for (let i = 0; i < json.length; i++) {
                if (json[i] === ','
                    && !insideString(json, i)
                    && !insideArray (json. slice(1, json. length - 1), i)
                    && !insideObject (json, i)) {
                    elementLocations .push (i) ;
                }
            }
            const elements = elementLocations.map ((Location, index) => {
                return (elementLocations [index + 1]) ?
                    json.slice(location + 1, elementLocations [index + 1]) :
                    json.slice(location + 1, json. length - 1);
            });
            return elements.map(element => parseJson(element)) ;
        }

        function parseJson(json) {
            const parseJSON = json => {
                json = json.trim();
                if (objectRegex.test(json)) {
                    return parseObject(json);
                } else if (arrayRegex.test(json)) {
                    return parseArray(json);
                } else if (stringRegex.test(json)) {
                    return parseString(json);
                } else if (numberRegex.test(json)) {
                    return parseNumber(json);
                } else if (trueRegex.test(json)) {
                    return true;
                } else if (falseRegex.test(json)) {
                    return false;
                } else if (nullegex.test(json)) {
                    return null;
                } else {
                    throw new SyntaxError;
                }
            }
        }
    */
    const handleChange = ({
                              target: {
                                  files: [file]
                              }
                          }) => {
        if (file) {
            handleFile(file);
        }
    };

    const handleFile = file => {
        let {name} = file;
        let ext = name.split(".").reverse()[0];
        const reader = new FileReader();
        reader.onload = () => {
            let {result} = reader;
            let index = result.indexOf("base64") + 7;
            let data = result.slice(index);
            setFile(data, name, ext);
        };
        reader.readAsDataURL(file);
    };

    const setFile = (file, name, ext) => {
        setData({
            file,
            name,
            ext
        });
    };

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
                                  query: 'select * from tis.samusers;'
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
                {current.subsystem === 'sql' ?
                    <Select
                        label={'Type Of Request:'}
                        value={current.request.selectedValue}
                        onChange={e => setCurrent({...current, request: e.target.value})}
                    >
                        <Option value={'sql'} label={'SQL'}/>
                    </Select> : <></>
                }
                {current.subsystem === 'file' ?
                    <>
                        <Select
                            label={'Type Of Request:'}
                            value={current.request.selectedValue}
                            onChange={e => setCurrent({...current, request: e.target.value})}
                        >
                            <Option value={'upload'} label={'Upload'}/>
                        </Select>
                        <Input type={'file'} onChange={handleChange}/>
                    </>
                    : <></>
                }
                <Input type={'submit'}/>
            </Form>
            <div style={{width: 700}}>
                <p>{response}</p>
            </div>
            <button onClick={logout}>
                Logout
            </button>
            <Counter/>
        </>
    );
}

export default MainPage;
