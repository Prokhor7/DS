package com.example.loggingservice;

import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.orbitz.consul.Consul;
import com.orbitz.consul.KeyValueClient;
import org.springframework.stereotype.Service;
import my.sharedclasses.Message;
import java.util.Map;
import java.util.UUID;

@Service
public class LoggingService {
    private HazelcastInstance hz = Hazelcast.newHazelcastInstance();
    private Consul consul = Consul.builder().build();
    private KeyValueClient kvClient = consul.keyValueClient();
    private Map<UUID, String> messages =hz.getMap(kvClient.getValueAsString("myMap").get());

    public void addToLog(Message msg) {
        messages.put(msg.getId(), msg.getTxt());
    }

    public Map<UUID, String> log() {
        return messages;
    }
}
