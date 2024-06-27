package it.polimi.ingsw.util;

/**
 * A record representing a chat message.
 *
 * @param sender The message's sender.
 * @param receiver The message's receiver.
 * @param msg The context of the message.
 */
public record Message(String sender,String receiver,String msg) {

}
