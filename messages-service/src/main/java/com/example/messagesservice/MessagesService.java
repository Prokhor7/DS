package com.example.messagesservice;

import com.hazelcast.collection.IQueue;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import org.springframework.stereotype.Service;
import my.sharedclasses.Message;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class MessagesService {
    private HazelcastInstance hz = Hazelcast.newHazelcastInstance();
    private IQueue<Message> queue = hz.getQueue("mq");
    private Map<UUID, String> messages = new ConcurrentHashMap<>();

    public MessagesService() {
        Thread receiverThread = new Thread(this::receiveMessages);
        receiverThread.start();
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
