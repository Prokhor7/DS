package com.example.facadeservice;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class FacadeController {
    private final FacadeService facadeService;

    public FacadeController(FacadeService facadeService) {
        this.facadeService = facadeService;
    }

    @GetMapping("/facade_service")
    public Mono<String> messages() {
        return facadeService.messages();
    }

    @PostMapping("/facade_service")
    public Mono<Void> addMessage(@RequestBody PayloadText text){
        return facadeService.addMessage(text);
    }
}
