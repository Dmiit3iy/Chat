package com.dmiit3iy.repository;

import javax.servlet.AsyncContext;
import javax.servlet.AsyncEvent;
import javax.servlet.AsyncListener;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class SSEEmittersRepository {
    private CopyOnWriteArrayList<AsyncContext> list = new CopyOnWriteArrayList<>();

    private static ConcurrentHashMap<String, CopyOnWriteArrayList<AsyncContext>> map = new ConcurrentHashMap<>();

    public void add(AsyncContext asyncContext, String login) {
        asyncContext.addListener(new AsyncListener() {
            @Override
            public void onComplete(AsyncEvent asyncEvent) {
                list.remove(asyncContext);
                System.out.println("Finish");
                removeFromMap(asyncContext, login);
            }

            @Override
            public void onTimeout(AsyncEvent asyncEvent) {
                list.remove(asyncContext);
                System.out.println("Timeout");
                removeFromMap(asyncContext, login);
            }

            @Override
            public void onError(AsyncEvent asyncEvent) {
                list.remove(asyncContext);
                System.out.println("Error");
                removeFromMap(asyncContext, login);
            }

            @Override
            public void onStartAsync(AsyncEvent asyncEvent) {
                System.out.println("Start async");
            }
        });

        list.add(asyncContext);
        System.out.println("After adding emitter " + list);
        addInMap(asyncContext, login);
        System.out.println("МАПА эмиттеров: "+map);
    }

    public CopyOnWriteArrayList<AsyncContext> getList() {
        return list;
    }

    public ConcurrentHashMap<String, CopyOnWriteArrayList<AsyncContext>> getMap() {
        return map;
    }

    public void clear() {
        this.list.clear();
    }

    public void addInMap(AsyncContext asyncContext, String login) {
        if(login!=null) {
            CopyOnWriteArrayList<AsyncContext> list = map.getOrDefault(login, new CopyOnWriteArrayList<>());
            list.add(asyncContext);
            map.put(login, list);
        }
    }


    public void removeFromMap(AsyncContext asyncContext, String login) {
        if(login!=null) {
            CopyOnWriteArrayList<AsyncContext> list = map.getOrDefault(login, new CopyOnWriteArrayList<>());
            list.remove(asyncContext);
            map.put(login, list);
        }
    }

    public void clearMap() {
        this.map.clear();
    }

    public static CopyOnWriteArrayList<String> getOnlineEmitters(){

        CopyOnWriteArrayList<String> list1 = new CopyOnWriteArrayList<>();
        for (var pair:map.entrySet()) {
            if(!pair.getValue().isEmpty()){
                list1.add(pair.getKey());
            }
        }
        return list1;
    }
}
