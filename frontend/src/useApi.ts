import { useState } from 'react';
import { Configuration, DocumentControllerApi, AssistantControllerApi } from './api';

const useApi = (backendUrl: string) => {

    const [documentApi] = useState<DocumentControllerApi>(
        new DocumentControllerApi(new Configuration({ basePath: backendUrl }))
    );

    const [assistantApi] = useState<AssistantControllerApi>(
        new AssistantControllerApi(new Configuration({ basePath: backendUrl }))
    );

    return { documentApi, assistantApi };
};

export default useApi;