package org.dmiit3iy.utils;

import com.launchdarkly.eventsource.MessageEvent;

import java.util.ArrayList;
import java.util.List;

public class EventOnlineWrapper {

    public static ArrayList<String> makeString(MessageEvent messageEvent){
        int length = messageEvent.getData().length();

        int x2 = messageEvent.getData().lastIndexOf(":")+3;
        String s = messageEvent.getData().substring(x2,length-3);
        String[] mass =s.split(",");
        ArrayList<String> list= new ArrayList<>(List.of(mass));
        return list;
    }
}
