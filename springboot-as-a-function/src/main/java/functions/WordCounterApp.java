package functions;

import functions.messaging.PubSubMessage;
import functions.messaging.SimplePublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.function.Consumer;

@Slf4j
@SpringBootApplication(scanBasePackages = "functions.*")
public class WordCounterApp {

    public static void main(String[] args) throws IOException {
        SpringApplication.run(WordCounterApp.class, args);
    }

    /**
     * This bean defines the functionality to handle the event from Pub/Sub topic
     */
    @Bean
    public Consumer<PubSubMessage> pubSubFunction(Environment env) {
        return message -> {
            // The PubSubMessage data field arrives as a base-64 encoded string and must be decoded.
            // See: https://cloud.google.com/functions/docs/calling/pubsub#event_structure
            String decodedMessage = new String(Base64.getDecoder().decode(message.getData()), StandardCharsets.UTF_8);
            log.info("Received: {}", decodedMessage);
            log.info("Words: {}", count(decodedMessage));
            try (SimplePublisher publisher = new SimplePublisher(env.getRequiredProperty("PROJECT_ID"), "cf-results")) {
                publisher.publish("Words: " + count(decodedMessage));
            } catch (IOException e) {
                log.error("failed to publish a message with error: {}", e.getMessage());
            }
        };
    }

    /**
     * This method defines the business logic to process the data in event from Pub/Sub topic
     */
    private int count(String message) {
        if (message == null) {
            return 0;
        }
        return message.split("\\s+").length;
    }


}
