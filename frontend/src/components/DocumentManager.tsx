import React, {useState, useEffect, useRef} from 'react';
import useApi from '../hooks/useApi.ts';
import {DocumentDTO, UploadFileRequest} from "../api";
import './DocumentManager.css';

// @ts-ignore
function DocumentManager() {
    const [selectedFile, setSelectedFile] = useState<File | null>(null);
    const [documents, setDocuments] = useState<DocumentDTO[]>([]);

    const fileInputRef = useRef<HTMLInputElement | null>(null);
    const {documentApi} = useApi('http://localhost:8080');

    const [showModal, setShowModal] = useState(false);
    const [name, setName] = useState('');
    const [content, setContent] = useState('');

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

    const handleContentChange = (e: React.ChangeEvent<HTMLTextAreaElement>) => {
        setContent(e.target.value);
    };

    const handleSubmit = async () => {
        await documentApi.uploadText({name, content});
        setShowModal(false);
        await fetchDocuments();
    }

    const fetchDocuments = async () => {
        const response = await documentApi.getAll();
        setDocuments(response.data);
    };


    function downloadDocument(id: number) {
        documentApi.download(id).then((response) => {
            const url = window.URL.createObjectURL(new Blob([response.data]));
            const link = document.createElement('a');
            link.href = url;
            link.setAttribute('download', 'document.txt');
            document.body.appendChild(link);
            link.click();
        });
    }


    const deleteDocument = async (id: number)=> {
        await documentApi._delete(id);
        await fetchDocuments();
    }

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
                    <div>
                        <h3>Insert Document as Text</h3>
                    </div>
                    <div><span>Unique Name of the document:</span></div>
                    <div><input type="text" value={name} onChange={handleNameChange} placeholder="---"/></div>
                    <div><span>Text:</span></div>
                    <div><textarea className="text-area" value={content} onChange={handleContentChange} placeholder="---"/></div>
                    <div className="button-container">
                        <button className="button-document" onClick={() => setShowModal(false)}>Cancel</button>
                        <button className="button-document" onClick={handleSubmit}>Submit</button>
                    </div>
                </div>
            )}
            <input type="file" ref={fileInputRef} onChange={handleFileChange} style={{display: 'none'}}/>
            <div className="button-container">
                <button className="button-document" onClick={handleUploadClick}>Upload</button>
                <button className="button-document" onClick={handleInsertTextClick}>Insert Document as Text</button>
            </div>

            <hr style={{width: '100%', borderColor: '#535bf2'}}/>

            <table>
                <thead>
                <tr>
                    <th>Name</th>
                    <th>Size</th>
                    <th>Download</th>
                    <th>Delete</th>
                </tr>
                </thead>
                <tbody>
                {documents.map((doc, index) => (
                    <tr key={index} style={{textAlign: 'center'}}>
                        <td>{doc.name}</td>
                        <td>{doc.size}</td>
                        <td>
                            <button className="button-document" onClick={() => downloadDocument(doc.id)}>Download</button>
                        </td>
                        <td>
                            <button className="button-document" onClick={() => deleteDocument(doc.id)}>Delete</button>
                        </td>
                    </tr>
                ))}
                </tbody>
            </table>
        </div>
    );
}

export default DocumentManager;