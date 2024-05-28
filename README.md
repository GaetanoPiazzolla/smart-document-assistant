# Smart Document Assistant

## Code formatting

```shell
./gradlew spotlessJavaApply
```

## Documentation and Typescript Stubs Generation

```shell
curl -o ./api/api-docs.yaml http://localhost:8080/api-docs
./gradlew openApiGenerate
```