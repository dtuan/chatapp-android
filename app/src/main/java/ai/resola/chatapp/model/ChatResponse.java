package ai.resola.chatapp.model;

import java.util.Arrays;

/**
 * Created by dotuan on 2017/03/22.
 */

public class ChatResponse {

    private String[] response;

    private String[] reply;

    private String text;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String[] getResponse() {
        return response;
    }

    public void setResponse(String[] response) {
        this.response = response;
    }

    public String[] getReply() {
        return reply;
    }

    public void setReply(String[] reply) {
        this.reply = reply;
    }

    @Override
    public String toString() {
        return "ChatResponse{" +
                "response=" + Arrays.toString(response) +
                ", reply=" + Arrays.toString(reply) +
                ", text='" + text + '\'' +
                '}';
    }
}
