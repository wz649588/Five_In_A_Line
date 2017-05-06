package com.example.fiveinarow;

/**
 * Created by wz649 on 2017/4/25.
 */

public class ChatMessage {
    private String messageText;
    private String messageUser;
    private String messageTime;
    private boolean read = false;

    public ChatMessage(String messageText, String messageUser, String messageTime) {
        this.messageText = messageText;
        this.messageUser = messageUser;
        this.messageTime = messageTime;
    }

    public ChatMessage() {
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public String getMessageUser() {
        return messageUser;
    }

    public void setMessageUser(String messageUser) {
        this.messageUser = messageUser;
    }

    public String getMessageTime() {
        return messageTime;
    }

    public void setMessageTime(String messageTime) {
        this.messageTime = messageTime;
    }

    public boolean isRead() {
        return read;
    }


}
