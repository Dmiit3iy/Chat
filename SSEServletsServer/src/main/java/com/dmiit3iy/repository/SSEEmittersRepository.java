package com.dmiit3iy.repository;


import com.dmiit3iy.DAO.DAO;
import com.dmiit3iy.dto.Event;
import com.dmiit3iy.model.User;
import com.dmiit3iy.service.OnlineService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.AsyncContext;
import javax.servlet.AsyncEvent;
import javax.servlet.AsyncListener;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;


public class SSEEmittersRepository {

    private static ConcurrentHashMap<Long, CopyOnWriteArrayList<AsyncContext>> map = new ConcurrentHashMap<>();

    public static void add(AsyncContext asyncContext, long userId) {
        ObjectMapper objectMapper = new ObjectMapper();
        asyncContext.addListener(new AsyncListener() {
            @Override
            public void onComplete(AsyncEvent asyncEvent) {
                //TODO remove from map
                remove(asyncContext, userId);
                System.out.println("Finish");

                try {
                    ArrayList<User> userArrayList = new ArrayList<>();
                    List<Long> list = getOnlineEmitters();
                    for (Long x : list) {
                        userArrayList.add((User) DAO.getObjectById(x, User.class));
                    }
                    String emmitors = objectMapper.writeValueAsString(userArrayList);
                    OnlineService.addEvent(new Event(String.valueOf(userId), emmitors));
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void onTimeout(AsyncEvent asyncEvent) {
                //TODO remove from map
                remove(asyncContext, userId);
                System.out.println("Timeout");

                try {
                    ArrayList<User> userArrayList = new ArrayList<>();
                    List<Long> list = getOnlineEmitters();
                    for (Long x : list) {
                        userArrayList.add((User) DAO.getObjectById(x, User.class));
                    }
                    String emmitors = objectMapper.writeValueAsString(userArrayList);
                    OnlineService.addEvent(new Event(String.valueOf(userId), emmitors));
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void onError(AsyncEvent asyncEvent) {
                remove(asyncContext, userId);
                System.out.println("Error");

                try {
                    ArrayList<User> userArrayList = new ArrayList<>();
                    List<Long> list = getOnlineEmitters();
                    for (Long x : list) {
                        userArrayList.add((User) DAO.getObjectById(x, User.class));
                    }
                    String emmitors = objectMapper.writeValueAsString(userArrayList);
                    OnlineService.addEvent(new Event(String.valueOf(userId), emmitors));
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }

            }

            @Override
            public void onStartAsync(AsyncEvent asyncEvent) {
                System.out.println("Start async");
            }
        });

        addInMap(asyncContext, userId);

        try {
            ArrayList<User> userArrayList = new ArrayList<>();
            List<Long> list = getOnlineEmitters();
            for (Long x : list) {
                userArrayList.add((User) DAO.getObjectById(x, User.class));
            }
            String emmitors = objectMapper.writeValueAsString(userArrayList);
            OnlineService.addEvent(new Event(String.valueOf(userId), emmitors));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        System.out.println("МАПА эмиттеров: " + map);


    }


    public ConcurrentHashMap<Long, CopyOnWriteArrayList<AsyncContext>> getMap() {
        return map;
    }

    public void clear() {
        this.map.clear();
    }

    public static void addInMap(AsyncContext asyncContext, long userId) {
        CopyOnWriteArrayList<AsyncContext> list = map.getOrDefault(userId, new CopyOnWriteArrayList<>());
        list.add(asyncContext);
        map.put(userId, list);
    }


    public static void remove(AsyncContext asyncContext, long userId) {
        if (userId > 0) {
            CopyOnWriteArrayList<AsyncContext> list = map.getOrDefault(userId, new CopyOnWriteArrayList<>());
            list.remove(asyncContext);
            map.put(userId, list);
        }
    }

    public static void clearMap() {
        map.clear();
    }

    /**
     * Метод для получения он-лайн пользователей (те у кого есть соединения - он-лайн)
     * @return
     */
    public static ArrayList<Long> getOnlineEmitters() {

        ArrayList<Long> list1 = new ArrayList<>();
        for (var pair : map.entrySet()) {
            if (!pair.getValue().isEmpty()) {
                list1.add(pair.getKey());
            }
        }
        return list1;
    }
}
