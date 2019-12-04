package com.hnu.hi;

public class Msg {
    public static final int type_received = 0;
    public static final int type_sent = 1;
    private String content;
    private int type;

    public Msg(String content, int type) {
        this.content = content;
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public String getContent() {
        return content;
    }
}

