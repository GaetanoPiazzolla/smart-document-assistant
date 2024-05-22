import {useState, useEffect} from 'react';
import './chat.css';
import useApi from './useApi';
import {ChatMessage} from "./api";

function Chat() {
    const {assistantApi} = useApi('http://localhost:8080');

    const [chatId, setChatId] = useState('');
    const [messages, setMessages] = useState<ChatMessage[]>([]);
    const [input, setInput] = useState('');

    // Generate a unique chatId on component load
    useEffect(() => {
        setChatId(Date.now().toString());
    }, []);

    const handleInput = (e: React.ChangeEvent<HTMLInputElement>) => {
        setInput(e.target.value);
    };

    const handleEnter = async (e: React.KeyboardEvent<HTMLInputElement>) => {
        if (e.key !== 'Enter')
            return;

        try {
            const message: ChatMessage = {
                chatId,
                message: input,
                isResponse: false
            };

            setMessages((prevMessages) => [...prevMessages, message]);

            const response = await assistantApi?.chat(message);
            if(response)
                setMessages((prevMessages) => [...prevMessages, response.data]);

            setInput('');

        } catch (error) {
            console.error(error);
        }
    };

    return (
        <div className="chat-container">
            <h2>Smart Document Assistant</h2>
            <hr style={{width: '100%'}}/>
            <div className="chat-messages">
                {messages.map((message, index) => (
                    <p key={index}
                       className={message.isResponse ? 'sent-message' : 'received-message'}>{message.message}</p>
                ))}
            </div>
            <input className="chat-input" type="text" value={input} onChange={handleInput} onKeyDown={handleEnter}/>
        </div>
    );
}

export default Chat;