import { useState } from 'react';
import { Configuration, DocumentControllerApi, AssistantControllerApi } from '../api';

const useApi = (backendUrl: string) => {
    const config = new Configuration({ basePath: backendUrl });

    const [documentApi] = useState<DocumentControllerApi>(
        new DocumentControllerApi(config)
    );

    const [assistantApi] = useState<AssistantControllerApi>(
        new AssistantControllerApi(config)
    );

    return { documentApi, assistantApi };
};

export default useApi;