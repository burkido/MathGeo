package com.example.mathgeo;

public class MessageModel {

    private String from, text, type;

    public MessageModel(String from, String text, String type) {
        this.from = from;
        this.text = text;
        this.type = type;

    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public MessageModel(){

    }

    @Override
    public String toString() {
        return "MessageModel{" +
                "from='" + from + '\'' +
                ", text='" + text + '\'' +
                '}';
    }
}
