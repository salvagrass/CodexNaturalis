package it.polimi.ingsw.util;

public class Message {
    public final String sender;
    public final String receiver;

    public final String msg;

    public Message(String sender, String receiver, String msg){
        this.sender = sender;
        this.receiver = receiver;
        this.msg = msg;
    }
}
