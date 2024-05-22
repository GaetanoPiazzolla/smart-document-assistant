import React, {useState, useEffect, useRef} from 'react';
import useApi from './useApi';
import {UploadFileRequest} from "./api";
import './documentUploader.css';

function DocumentUploader() {
    const [selectedFile, setSelectedFile] = useState<File | null>(null);
    const [documents, setDocuments] = useState<any[]>([]);
    const [showModal, setShowModal] = useState(false);
    const [name, setName] = useState('');
    const [content, setContent] = useState('');
    const fileInputRef = useRef<HTMLInputElement | null>(null);
    const {documentApi} = useApi('http://localhost:8080');

    const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        setSelectedFile(e.target.files ? e.target.files[0] : null);
    };

    const handleUploadClick = () => {
        fileInputRef.current?.click();
    };

    const handleInsertTextClick = () => {
        setShowModal(true);
    }

    const handleNameChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        setName(e.target.value);
    };

    const handleContentChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        setContent(e.target.value);
    };

    const handleSubmit = async () => {
        // Call the documentApi with the name and content
        await documentApi.uploadText({name, content});
        // Close the modal
        setShowModal(false);
    }

    const fetchDocuments = async () => {
        const response = await documentApi.getAll();
        setDocuments(response.data);
    };

    const handleUpload = async () => {
        if (selectedFile) {
            const uploadFileRequest: UploadFileRequest = {
                file: selectedFile
            };
            const options = {
                headers: {
                    'Content-Type': 'multipart/form-data'
                }
            }
            await documentApi.uploadFile(selectedFile.name, uploadFileRequest, options);
            await fetchDocuments();
        }
    };

    useEffect(() => {
        fetchDocuments();
    }, [documentApi]);

    useEffect(() => {
        if (selectedFile) {
            handleUpload();
        }
    }, [selectedFile]);

    return (
        <div className="document-uploader-container">
            {showModal && (
                <div className="modal">
                    <input type="text" value={name} onChange={handleNameChange} placeholder="Name" />
                    <input type="text" value={content} onChange={handleContentChange} placeholder="Content" />
                    <button onClick={handleSubmit}>Submit</button>
                </div>
            )}
            <input type="file" ref={fileInputRef} onChange={handleFileChange} style={{display: 'none'}}/>
            <button onClick={handleUploadClick}>Upload</button>
            <button onClick={handleInsertTextClick}>Insert Text</button>
            <table>
                <thead>
                <tr>
                    <th>Document Name</th>
                    <th>Document Size</th>
                </tr>
                </thead>
                <tbody>
                {documents.map((doc, index) => (
                    <tr key={index} style={{textAlign: 'center'}}>
                        <td>{doc.name}</td>
                        <td>{doc.size}</td>
                    </tr>
                ))}
                </tbody>
            </table>
        </div>
    );
}

export default DocumentUploader;