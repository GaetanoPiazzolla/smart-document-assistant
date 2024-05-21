# Smart Document Assistant

## Code formatting
To format code it's enough to run the gradle command spotlessJavaApply:
```shell
./gradlew spotlessJavaApply
```
FAQ for this formatting can be found here: https://github.com/google/google-java-format/wiki/FAQ

## Documentation and Typescript Stubs Generation
The documentation of the API is generated using Swagger.
To access the SWAGGER documentation go to `http://localhost:8080/swagger-ui.html` and
for open-api YAML documentation go to `http://localhost:8080/api-docs` for Json and http://localhost:8080/api-docs.yaml for YAML.

Or you can find the generated documentation in the `api` folder.

To update the typescript types for the frontend run the following command you need to update the api-docs.yaml file with the current version of the API,
then run the generation.

Run the following script with the application running on port 8081:
```shell
curl -o ./api/api-docs.yaml http://localhost:8080/api-docs
./gradlew openApiGenerate
```

## Stubs usage example in FE:

```typescript
import { Configuration, **ControllerApi } from '@/lib/api';

const configuration = new Configuration({
    basePath: backendUrl,
});

http.skrControllerApi = new **ControllerApi(configuration);

const options = {
    headers: {
         Authorization: .... 
    },
};

await http.**tControllerApi.method(options)).data;
```