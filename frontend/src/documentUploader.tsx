import { useState, useEffect, useRef } from 'react';
import useApi from './useApi';
import {UploadFileRequest} from "./api";
import './documentUploader.css';

function DocumentUploader() {
    const { documentApi } = useApi('http://localhost:8080');
    const [selectedFile, setSelectedFile] = useState<File | null>(null);
    const [documents, setDocuments] = useState<any[]>([]);

    const fileInputRef = useRef<HTMLInputElement | null>(null);

    const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        setSelectedFile(e.target.files ? e.target.files[0] : null);
    };

    const handleUploadClick = () => {
        fileInputRef.current?.click();
    };

    const handleUpload = async () => {
        if (selectedFile && documentApi) {
            const uploadFileRequest: UploadFileRequest = {
                file: selectedFile
            };
            const options = {
                headers: {
                    'Content-Type': 'multipart/form-data'
                }
            }
            await documentApi.uploadFile(selectedFile.name, uploadFileRequest, options);
        }
    };

    useEffect(() => {
        const fetchDocuments = async () => {
            if (documentApi) {
                const response = await documentApi.getAll();
                setDocuments(response.data);
            }
        };

        fetchDocuments();
    }, [documentApi]);

    useEffect(() => {
        if (selectedFile) {
            handleUpload();
        }
    }, [selectedFile]);

    return (
        <div className="document-uploader-container">
            <input type="file" ref={fileInputRef} onChange={handleFileChange} style={{ display: 'none' }} />
            <button onClick={handleUploadClick}>Upload</button>
            <table>
                <thead>
                <tr>
                    <th>Document Name</th>
                    <th>Document Size</th>
                </tr>
                </thead>
                <tbody>
                {documents.map((doc, index) => (
                    <tr key={index}>
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