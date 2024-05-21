package gae.piaz.sda.springai;

import java.io.IOException;
import java.util.List;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

@Configuration
public class SpringAiConfig {

    @Bean
    CommandLineRunner ingestDocsForSpringAi(VectorStore vectorStore, ResourceLoader resourceLoader)
            throws IOException {

        return args -> {
            Resource resource = resourceLoader.getResource("classpath:example-document.txt");

            // Ingest the document into the vector store
            // stores
            List<Document> documents =
                    new TokenTextSplitter(30, 20, 1, 10000, true)
                            .apply(new TextReader(resource).get());

            vectorStore.accept(documents);
        };
    }
}
