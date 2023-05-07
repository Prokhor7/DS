package com.example.facadeservice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Random;
import java.util.UUID;

@Service
public class FacadeService {
    Logger logger = LoggerFactory.getLogger(FacadeService.class);

    private List<WebClient> loggingWebClients;
    private WebClient messagesWebClient;

    public FacadeService() {
        loggingWebClients = List.of(
                WebClient.create("http://localhost:8083"),
                WebClient.create("http://localhost:8084"),
                WebClient.create("http://localhost:8085")
        );
        messagesWebClient = WebClient.create("http://localhost:8082");
    }

    public Mono<Void> addMessage(PayloadText text) {
        var msg = new Message(UUID.randomUUID(), text.getText());

        var loggingWebClient = getRandomLoggingClient();
        logger.info(loggingWebClient.toString());

        return loggingWebClient.post()
                .uri("/log")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(msg), Message.class)
                .retrieve()
                .bodyToMono(Void.class);
    }

    public Mono<String> messages() {
        var loggingWebClient = getRandomLoggingClient();

        var logValuesMono = loggingWebClient.get()
                .uri("/log")
                .retrieve()
                .bodyToMono(String.class);

        var messageMono = messagesWebClient.get()
                .uri("/message")
                .retrieve()
                .bodyToMono(String.class);

        return logValuesMono.zipWith(messageMono,
                        (logValues, message) -> logValues + ": " + message)
                .onErrorReturn("Error");
    }

    private WebClient getRandomLoggingClient() {
        Random random = new Random();
        int index = random.nextInt(loggingWebClients.size());
        return loggingWebClients.get(index);
    }
}
