import {useState, useEffect} from 'react';
import './Chat.css';
import useApi from '../hooks/useApi.ts';
import {ChatMessage} from "../api";

function Chat() {
    const {assistantApi} = useApi('http://localhost:8080');

    const [chatId, setChatId] = useState('');
    const [messages, setMessages] = useState<ChatMessage[]>([{
        chatId: "0",
        message: "Hello! How can I help you today?",
        isResponse: true
    }]);
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

            setMessages((prevMessages) => [message, ...prevMessages ]);
            setInput('');

            const response = await assistantApi?.chat(message);
            if(response)
                setMessages((prevMessages) => [response.data, ...prevMessages]);


        } catch (error) {
            console.error(error);
        }
    };

    return (
        <div className="chat-container">
            <div className="title">
                <h2>Smart Document Assistant</h2>
            </div>
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