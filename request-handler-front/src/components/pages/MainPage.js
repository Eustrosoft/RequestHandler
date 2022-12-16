import React, {useState} from 'react'
import Header from "../ui/Header/Header";
import Form from "../ui/Form/Form";
import Input from "../ui/Input/Input";
import Counter from "../ui/Counter/Counter";
import TextArea from "../ui/Input/TextArea";
import Select from "../ui/Select/Select";
import Option from "../ui/Select/Option";

function MainPage() {
    const [query, setQuery] = useState({
        query: '',
        type: ''
    });

    const log = (e) => {
        e.preventDefault();
        console.log(JSON.stringify(query));
    }

    return (
        <>
            <Header>
                Welcome to Request Dispatcher!
            </Header>
            <Form onSubmit={log}>
                <TextArea label={'Query:'}
                          value={query.query}
                          onChange={e => setQuery({...query, query: e.target.value})}/>
                <Select
                    label={'Type Of Query:'}
                    value={query.type}
                    onChange={e => setQuery({...query, type: e.target.value})}
                >
                    <Option value={'sql'} label={'SQL query'}/>
                    <Option value={'file'} label={'File Query'}/>
                </Select>
                <Input type={'submit'}/>
            </Form>
            <Counter/>
        </>
    );
}

export default MainPage;
