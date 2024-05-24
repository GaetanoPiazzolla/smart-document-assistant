import './App.css'
import Chat from "./components/Chat.tsx";
import DocumentManager from "./components/DocumentManager.tsx";
function App() {
    return (
        <>
            <div className="app-container">
                <Chat/>
                <DocumentManager/>
            </div>
        </>
    )
}

export default App
