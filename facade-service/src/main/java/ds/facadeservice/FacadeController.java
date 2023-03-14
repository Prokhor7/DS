package ds.facadeservice;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
public class FacadeController {
    WebClient loggingWebClient = WebClient.create("http://localhost:8082");
    WebClient messagesWebClient = WebClient.create("http://localhost:8083");

    @GetMapping("facade_service")
    public Mono<String> clientWebClient(){

        Mono<String> cashedValues = loggingWebClient.get()
                .uri("/log")
                .retrieve()
                .bodyToMono(String.class);

        Mono<String> messageMono = messagesWebClient.get()
                .uri("/message")
                .retrieve()
                .bodyToMono(String.class);

        System.out.println("\nGETTING INFO");

        return cashedValues.zipWith(messageMono,
                        (cached, message) -> cached +": "+ message)
                .onErrorReturn("Error");
    }
    @PostMapping("/facade_service")
    public Mono<Void> facadeWebClient(@RequestBody PayloadText text){

        var msg = new Message(UUID.randomUUID(), text.getTxt());

        System.out.println("\n!NEW MESSAGE!\nID: "+msg.getId()+"\nTEXT: "+msg.getTxt());

        return loggingWebClient.post()
                .uri("/log")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(msg), Message.class)
                .retrieve()
                .bodyToMono(Void.class);
    }


}

