package com.example.facadeservice;

import com.hazelcast.collection.IQueue;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import my.sharedclasses.Message;
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
    private List<WebClient> messagesWebClients;
    private HazelcastInstance hz = Hazelcast.newHazelcastInstance();
    private IQueue<Message> queue = hz.getQueue("mq");

    public FacadeService() {
        loggingWebClients = List.of(
                WebClient.create("http://localhost:8082"),
                WebClient.create("http://localhost:8083"),
                WebClient.create("http://localhost:8084")
        );
        messagesWebClients = List.of(
                WebClient.create("http://localhost:8085"),
                WebClient.create("http://localhost:8086")
        );
    }

    public Mono<Void> addMessage(PayloadText text) {
        var msg = new Message(UUID.randomUUID(), text.getText());

        var loggingWebClient = getRandomLoggingClient();
        logger.info(loggingWebClient.toString());

        try {
            queue.put(msg);
        } catch (InterruptedException e) {
            System.out.println("Error");
        }

        return loggingWebClient.post()
                .uri("/log")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(msg), Message.class)
                .retrieve()
                .bodyToMono(Void.class);
    }

    public Mono<String> messages() {
        var loggingWebClient = getRandomLoggingClient();
        var messagesWebClient = getRandomMessagesClient();
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
        //index = 0;
        return loggingWebClients.get(index);
    }

    private WebClient getRandomMessagesClient(){
        Random random = new Random();
        int index = random.nextInt(messagesWebClients.size());
        //index = 0;
        return messagesWebClients.get(index);
    }
}
