package com.dmiit3iy.dto;

import java.util.StringJoiner;

public class Event {

    private final String action;
    private final String path;

    public Event(String action, String path) {
        this.action = action;
       // this.path = path.toString();
        this.path = path;
    }

    public String getAction() {
        return action;
    }

    public String getPath() {
        return path;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Event.class.getSimpleName() + "[", "]")
                .add("action='" + action + "'")
                .add("path='" + path + "'")
                .toString();
    }
}