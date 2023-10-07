package org.dmiit3iy.utils;

import com.launchdarkly.eventsource.MessageEvent;
import org.dmiit3iy.model.Msg;

public class EventWrapper {

    public static String makeString(Msg msg){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("@"+msg.getUser().getLogin()+"\n");
        stringBuilder.append(msg.getLocalDateTime()+"\n");
        stringBuilder.append(msg.getMessage());
        return stringBuilder.toString();
    }
}
