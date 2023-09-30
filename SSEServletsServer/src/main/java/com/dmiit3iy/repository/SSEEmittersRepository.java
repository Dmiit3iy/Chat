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

    private ConcurrentHashMap<String, CopyOnWriteArrayList<AsyncContext>> map = new ConcurrentHashMap<>();

    public void add(AsyncContext asyncContext, String idUser) {
        asyncContext.addListener(new AsyncListener() {
            @Override
            public void onComplete(AsyncEvent asyncEvent) {
                list.remove(asyncContext);
                System.out.println("Finish");
                removeFromMap(asyncContext, idUser);
            }

            @Override
            public void onTimeout(AsyncEvent asyncEvent) {
                list.remove(asyncContext);
                System.out.println("Timeout");
                removeFromMap(asyncContext, idUser);
            }

            @Override
            public void onError(AsyncEvent asyncEvent) {
                list.remove(asyncContext);
                System.out.println("Error");
                removeFromMap(asyncContext, idUser);
            }

            @Override
            public void onStartAsync(AsyncEvent asyncEvent) {
                System.out.println("Start async");
            }
        });

        list.add(asyncContext);
        System.out.println("After adding emitter " + list);
        addInMap(asyncContext, idUser);
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

    public void addInMap(AsyncContext asyncContext, String idUser) {
        if(idUser!=null) {
            CopyOnWriteArrayList<AsyncContext> list = map.getOrDefault(idUser, new CopyOnWriteArrayList<>());
            list.add(asyncContext);
            map.put(idUser, list);
        }
    }

    public void removeFromMap(AsyncContext asyncContext, String idUser) {
        if(idUser!=null) {
            CopyOnWriteArrayList<AsyncContext> list = map.getOrDefault(idUser, new CopyOnWriteArrayList<>());
            list.remove(asyncContext);
            map.put(idUser, list);
        }
    }

    public void clearMap() {
        this.map.clear();
    }
}
