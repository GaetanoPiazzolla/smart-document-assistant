import {useState, useEffect} from 'react';
import axios from 'axios';
import './chat.css';

function Chat() {
    const [chatId, setChatId] = useState('');
    const [messages, setMessages] = useState([]);
    const [input, setInput] = useState('');

    // Generate a unique chatId on component load
    useEffect(() => {
        setChatId(Date.now().toString());
    }, []);

    const handleInput = (e) => {
        setInput(e.target.value);
    };

    const handleEnter = async (e) => {
        if (e.key === 'Enter') {
            try {
                const message = {
                    chatId,
                    message: input,
                }
                setMessages((prevMessages) => [...prevMessages, {...message, response: false}]);
                const response = await axios.post('http://localhost:8080/chat', message);
                setMessages((prevMessages) => [...prevMessages, { message: response.data, response: true}]);
                setInput('');
            } catch (error) {
                console.error(error);
            }
        }
    };

    return (
        <div className="chat-container">
            <h2>Smart Document Assistant</h2>
            <hr style={{width: '100%'}}/>
            <div className="chat-messages">
                {messages.map((message, index) => (
                    <p key={index}
                       className={message.response ? 'sent-message' : 'received-message'}>{message.message}</p>
                ))}
            </div>
            <input className="chat-input" type="text" value={input} onChange={handleInput} onKeyDown={handleEnter}/>
        </div>
    );
}

export default Chat;