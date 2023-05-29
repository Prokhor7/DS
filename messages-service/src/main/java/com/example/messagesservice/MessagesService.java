package com.example.messagesservice;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.collection.IQueue;
import com.hazelcast.core.HazelcastInstance;
import com.orbitz.consul.Consul;
import com.orbitz.consul.KeyValueClient;
import org.springframework.stereotype.Service;
import my.sharedclasses.Message;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class MessagesService {
    private ExecutorService executorService;
    private HazelcastInstance hz = HazelcastClient.newHazelcastClient();
    private Consul consul = Consul.builder().build();
    private KeyValueClient kvClient = consul.keyValueClient();
    private IQueue<Message> queue = hz.getQueue(kvClient.getValueAsString("myMq").get());
    private Map<UUID, String> messages = new ConcurrentHashMap<>();

    public MessagesService() {
        /*Thread receiverThread = new Thread(this::receiveMessages);
        receiverThread.start();*/
        executorService = Executors.newSingleThreadExecutor();
        executorService.submit(this::receiveMessages);
    }

    public String getMessages() {
        return messages.values().toString();
    }

    private void receiveMessages() {
        Message msg;
        while (true) {
            msg = queue.poll();
            if (msg != null) {
                System.out.println("Received message: " + msg.getTxt());
                messages.put(msg.getId(), msg.getTxt());
            }
        }
    }
}
