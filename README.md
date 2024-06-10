# Smart Document Assistant

A Retrieval Augmented Generation (RAG) Tool with document management using Spring Boot, ReactJS, and PGVector.

- Introduction: https://gaetanopiazzolla.github.io/java/rag/2024/05/29/rag-spring-ai.html
- Function Calling: https://gaetanopiazzolla.github.io/java/rag/2024/06/10/llm-function-calling.html

## How to Run Locally
Run the following command to start the backend application:

```shell
./gradlew bootRun
```

And this one to start the ReactJS application:

```shell
cd frontend
npm run dev
```

## Code formatting

```shell
./gradlew spotlessJavaApply
```

## Documentation and Typescript Stubs Generation

```shell
curl -o ./api/api-docs.yaml http://localhost:8080/api-docs
./gradlew openApiGenerate
```
