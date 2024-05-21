import { useState, useEffect } from 'react';
import { Configuration, DocumentControllerApi, AssistantControllerApi } from './api';

const useApi = (backendUrl: string) => {
    const [documentApi, setDocumentApi] = useState<DocumentControllerApi | null>(null);
    const [assistantApi, setAssistantApi] = useState<AssistantControllerApi | null>(null);

    useEffect(() => {
        const configuration = new Configuration({
            basePath: backendUrl,
        });

        const documentControllerApi = new DocumentControllerApi(configuration);
        const assistantControllerApi = new AssistantControllerApi(configuration);

        setDocumentApi(documentControllerApi);
        setAssistantApi(assistantControllerApi);
    }, [backendUrl]);

    return { documentApi, assistantApi };
};

export default useApi;