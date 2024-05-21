import './App.css'
import Chat from "./chat.tsx";
import DocumentUploader from "./documentUploader.tsx";
function App() {
    return (
        <>
            <div className="app-container">
                <Chat/>
                <DocumentUploader/>
            </div>
        </>
    )
}

export default App
