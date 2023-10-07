package org.dmiit3iy.dto;

import java.util.StringJoiner;

public class Event {

//    private final String user;
//   // private final String action;
//    private final String message;
    private  String user;
    // private final String action;
    private  String message;

    public Event() {
    }

    public Event(String user, String message) {
        this.user = user;
       // this.path = path.toString();
        this.message = message;
    }

    public String getUser() {
        return user;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Event.class.getSimpleName() + "[", "]")
                .add("User'" + user + "'")
                .add(":" + message + "'")
                .toString();
    }
}