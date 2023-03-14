package ds.loggingservice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@RestController
public class LoggingController {
    Logger logger = LoggerFactory.getLogger(LoggingController.class);

    private Map<UUID, String> messages = new ConcurrentHashMap<>();

    @GetMapping("/log")
    public String listLog(){ return messages.values().toString(); }

    @PostMapping("/log")
    public ResponseEntity<Void> log(@RequestBody Message msg){
        logger.info(msg.toString());
        messages.put(msg.getId(), msg.getTxt());
        return ResponseEntity.ok().build();
    }


}

