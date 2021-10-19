package model;

import java.io.Serializable;

public class Message implements Serializable {
    String type;
    Object payload;

    public void setType(String type) {
        this.type = type;
    }

    public Message(String type, Object payload) {
        this.type = type;
        this.payload = payload;
    }

    @Override
    public String toString() {
        return "Message{" +
                "type='" + type + '\'' +
                ", payload=" + payload +
                '}';
    }

    public void setPayload(Object payload) {
        this.payload = payload;
    }

    public String getType() {
        return type;
    }

    public Object getPayload() {
        return payload;
    }
}
