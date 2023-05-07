package com.example.loggingservice;

import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
public class LoggingService {
    private HazelcastInstance hz = Hazelcast.newHazelcastInstance();
    private Map<UUID, String> messages =hz.getMap("logging_map");

    public void addToLog(Message msg) {
        messages.put(msg.getId(), msg.getTxt());
    }

    public Map<UUID, String> log() {
        return messages;
    }
}
