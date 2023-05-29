package com.example.facadeservice;

import com.hazelcast.collection.IQueue;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import my.sharedclasses.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import com.orbitz.consul.Consul;
import com.orbitz.consul.KeyValueClient;

import java.util.List;
import java.util.Random;
import java.util.UUID;

@Service
public class FacadeService {
    Logger logger = LoggerFactory.getLogger(FacadeService.class);

    //private List<WebClient> loggingWebClients;
    //private List<WebClient> messagesWebClients;
    private DiscoveryClient discoveryClient;
    private HazelcastInstance hz = Hazelcast.newHazelcastInstance();
    private Consul consul = Consul.builder().build();
    private KeyValueClient kvClient = consul.keyValueClient();
    private IQueue<Message> queue;

    /*public FacadeService() {
        loggingWebClients = List.of(
                WebClient.create("http://localhost:8082"),
                WebClient.create("http://localhost:8083"),
                WebClient.create("http://localhost:8084")
        );
        messagesWebClients = List.of(
                WebClient.create("http://localhost:8085"),
                WebClient.create("http://localhost:8086")
        );
    }*/

    public FacadeService(DiscoveryClient discoveryClient){
        this.discoveryClient = discoveryClient;
        kvClient.putValue("myMap", "logging_map");
        kvClient.putValue("myMq", "mq");
        queue = hz.getQueue(kvClient.getValueAsString("myMq").get());
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

    /*private WebClient getRandomLoggingClient() {
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
    }*/

    private WebClient getRandomLoggingClient() {
        List<ServiceInstance> instances = discoveryClient.getInstances("logging-service");
        if (instances.isEmpty()) {
            throw new RuntimeException("No instances available for logging-service");
        }
        ServiceInstance instance = instances.get(new Random().nextInt(instances.size()));
        String url = instance.getUri().toString();
        return WebClient.create(url);
    }

    private WebClient getRandomMessagesClient() {
        List<ServiceInstance> instances = discoveryClient.getInstances("messages-service");
        if (instances.isEmpty()) {
            throw new RuntimeException("No instances available for messages-service");
        }
        ServiceInstance instance = instances.get(new Random().nextInt(instances.size()));
        String url = instance.getUri().toString();
        return WebClient.create(url);
    }

}
