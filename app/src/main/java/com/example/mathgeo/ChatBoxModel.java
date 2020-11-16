package com.example.mathgeo;

import java.io.Serializable;

public class ChatBoxModel implements Serializable {

    String receiverID, senderID, username;

    public ChatBoxModel(String receiverID, String senderID, String username) {
        this.receiverID = receiverID;
        this.senderID = senderID;
        this.username = username;
    }

    public String getReceiverID() {
        return receiverID;
    }

    public void setReceiverID(String receiverID) {
        this.receiverID = receiverID;
    }

    public String getSenderID() {
        return senderID;
    }

    public void setSenderID(String senderID) {
        this.senderID = senderID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public ChatBoxModel(){

    }
}
