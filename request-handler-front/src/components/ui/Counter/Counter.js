import React, {useState} from 'react';
import './Counter.css'

const Counter = () => {
    const [counter, setCounter] = useState(0)

    return (
        <div className='counter'>
            <p>If you have nothing to do - play counter!</p>
            <button onClick={() => setCounter(counter + 1)}>Click!</button>
            <span style={{paddingLeft: '15px', fontStyle: 'italic'}}>Counter: {counter}</span>
        </div>
    );
};

export default Counter;