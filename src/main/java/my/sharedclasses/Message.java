package my.sharedclasses;

import java.io.Serializable;
import java.util.UUID;

public class Message implements Serializable {
    private UUID id;
    private String txt;

    public UUID getId() {
        return id;
    }

    public String getTxt() {
        return txt;
    }

    public Message() {
        this.id = UUID.randomUUID();
        this.txt = "default";
    }


    public Message(UUID id, String txt) {
        this.id = id;
        this.txt = txt;
    }
    public static Message create(UUID id, String txt) {
        return new Message(id, txt);
    }

}