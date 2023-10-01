package com.dmiit3iy.service;

import com.dmiit3iy.dto.Event;
import com.dmiit3iy.repository.SSEEmittersRepository;
import com.dmiit3iy.repository.SSEOnlineEmittorsRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.AsyncContext;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

public class EmittersService {
    private SSEEmittersRepository repository;
    private SSEOnlineEmittorsRepository onlineEmittorsRepository;

    private BlockingQueue<Event> messageBlockingQueue = new LinkedBlockingQueue<>();

    private ExecutorService singleThreadExecutorTasker;
    private ExecutorService singleThreadExecutorWatcher;

    private void sendMessage(PrintWriter writer, Event message) {
        try {
            writer.println("data: " + new ObjectMapper().writeValueAsString(message));
            writer.println();
            writer.flush();
        } catch (Exception ignored) {
        }
    }

    public EmittersService(SSEOnlineEmittorsRepository onrepository) {
        this.onlineEmittorsRepository = onrepository;
        this.startMessageReceive();
        this.startWhatchEmitters();

    }

    private void startMessageReceive() {
        singleThreadExecutorTasker = Executors.newSingleThreadExecutor();
        singleThreadExecutorTasker.execute(() -> {
            try {
                while (true) {
                    Event message = messageBlockingQueue.take();
                    System.out.println("Start sending\n" + onlineEmittorsRepository.getList());
                    for (AsyncContext asyncContext : onlineEmittorsRepository.getList()) {
                        try {
                            sendMessage(asyncContext.getResponse().getWriter(), message);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            } catch (InterruptedException e) {
                System.out.println("Thread is interrupting");
            }
            System.out.println("Thread is interrupted");
        });
    }

    //Отслеживать состояние эмиттеров онлайн
    private void startWhatchEmitters() {
        singleThreadExecutorWatcher = Executors.newSingleThreadExecutor();
        singleThreadExecutorWatcher.execute(() -> {
            try {
                while (true) {
                    int size1 = repository.getOnlineEmitters().size();
                    System.out.println("размер из репозитория!!!"+size1);
                    Thread.sleep(1000);
                    int size2 = repository.getOnlineEmitters().size();
                    if ((size2 - size1) != 0) {

                        System.out.println("В эмиттор сервисе смотрим "+repository.getOnlineEmitters());
                        messageBlockingQueue.add(new Event("обновление", repository.getOnlineEmitters().toString()));
                    }
                }
            } catch (InterruptedException e) {
                //e.printStackTrace();
                //Thread.currentThread().interrupt();
                System.out.println("Thread is interrupting");
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println("Thread is interrupted");
        });
    }


    public void addEvent(Event e) {
        messageBlockingQueue.add(e);
    }

    public void stop() {
        this.singleThreadExecutorTasker.shutdownNow();
        this.onlineEmittorsRepository.clear();
        this.messageBlockingQueue.clear();
    }
}
