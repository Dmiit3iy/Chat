package org.dmiit3iy.utils;

import com.launchdarkly.eventsource.MessageEvent;

public class EventWrapper {


    public static String makeString(MessageEvent messageEvent){
        int length = messageEvent.getData().length();
        int x =  messageEvent.getData().indexOf(",");
        int x2 = messageEvent.getData().lastIndexOf(":");
        return "@"+messageEvent.getData().substring(9,x-1)+"\n"+
               messageEvent.getData().substring(length-21,length-2)+"\n"+
               messageEvent.getData().substring(x2+2,length-21);
    }
}
