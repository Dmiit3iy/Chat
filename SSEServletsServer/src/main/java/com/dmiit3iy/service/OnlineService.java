package com.dmiit3iy.service;

import com.dmiit3iy.dto.Event;
import com.dmiit3iy.repository.SSEEmittersRepository;
import com.dmiit3iy.repository.SSEOnlineRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.AsyncContext;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

public class OnlineService {
    private SSEOnlineRepository sseOnlineRepository;

    private static BlockingQueue<Event> messageBlockingQueue = new LinkedBlockingQueue<>();


    private ExecutorService singleThreadExecutorTasker;

    private void sendMessage(PrintWriter writer, Event message) {
        try {
            writer.println("data: " + new ObjectMapper().writeValueAsString(message));
            writer.println();
            writer.flush();
        } catch (Exception ignored) {
        }
    }

    public OnlineService(SSEOnlineRepository repository) {
        this.sseOnlineRepository = repository;
        this.startMessageReceive();

    }

    private void startMessageReceive() {
        singleThreadExecutorTasker = Executors.newSingleThreadExecutor();
        singleThreadExecutorTasker.execute(() -> {
            try {
                while (true) {
                    Event message = messageBlockingQueue.take();
                    System.out.println("Start sending\n" + sseOnlineRepository.getList());
                    for (AsyncContext asyncContext : sseOnlineRepository.getList()) {
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

    public static void addEvent(Event event) {
        messageBlockingQueue.add(event);

    }

    public void stop() {
        this.singleThreadExecutorTasker.shutdownNow();
        this.sseOnlineRepository.clear();
        this.messageBlockingQueue.clear();
    }

}
