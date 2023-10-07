package com.dmiit3iy.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.dmiit3iy.dto.Event;
import com.dmiit3iy.repository.SSEEmittersRepository;

import javax.servlet.AsyncContext;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

public class ChatService {
    private SSEEmittersRepository repository;

    private BlockingQueue<Event> messageBlockingQueue = new LinkedBlockingQueue<>();

    private ExecutorService singleThreadExecutorTasker;

    private void sendMessage(PrintWriter writer, Event message) {
        try {
            writer.println("data: " + new ObjectMapper().writeValueAsString(message));
            writer.println();
            writer.flush();
        } catch (Exception ignored) {
        }
    }

    public ChatService(SSEEmittersRepository repository) {
        this.repository=repository;
        this.startMessageReceive();

    }

    private void startMessageReceive() {
        singleThreadExecutorTasker = Executors.newSingleThreadExecutor();
        singleThreadExecutorTasker.execute(() -> {
            try {
                while (true) {
                    Event message = messageBlockingQueue.take();
                    System.out.println("Start sending\n" + repository.getMap());
                    for (var pair : repository.getMap().entrySet()) {

                        try {
                            for (AsyncContext asyncContext:pair.getValue()) {
                                sendMessage(asyncContext.getResponse().getWriter(), message);
                            }

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

 public void addEvent(Event e){
        messageBlockingQueue.add(e);
 }

    public void stop() {
        this.singleThreadExecutorTasker.shutdownNow();
        this.repository.clear();
        this.repository.clearMap();
        this.messageBlockingQueue.clear();
    }
}
