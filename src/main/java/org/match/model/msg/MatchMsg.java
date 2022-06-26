package org.match.model.msg;

import org.match.interfaces.IMsg;

public class MatchMsg implements IMsg {

    private String recipient;
    private String sender;
    private Object data;

    public MatchMsg(String recipient, String sender, Object data) {
        this.recipient = recipient;
        this.sender = sender;
        this.data = data;
    }

    @Override
    public String getRecipient() {
        return recipient;
    }

    @Override
    public String getSender() {
        return sender;
    }

    @Override
    public Object getData() {
        return data;
    }

    @Override
    public String toString() {
        return "MatchResult{" +
                "recipient='" + recipient + '\'' +
                ", sender='" + sender + '\'' +
                ", data=" + data +
                '}';
    }
}
