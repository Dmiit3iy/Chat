package org.dmiit3iy.controllers;

import com.launchdarkly.eventsource.EventHandler;
import com.launchdarkly.eventsource.EventSource;
import com.launchdarkly.eventsource.MessageEvent;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.control.TextArea;
import org.dmiit3iy.model.Msg;
import org.dmiit3iy.model.User;
import org.dmiit3iy.repositories.MsgRepository;
import org.dmiit3iy.utils.SimpleEventHandler;

import java.io.IOException;
import java.net.URI;
import java.util.concurrent.*;

public class ChatController {

    private BlockingQueue<String> blockingQueue = new LinkedBlockingQueue<>();
    public void setBlockingQueue(String s) throws InterruptedException {
        this.blockingQueue.put(s);
    }
    MsgRepository msgRepository = new MsgRepository();
    private String message;

    public String getEventMessage() {
        return EventMessage;
    }

    public void setEventMessage(String eventMessage) {
        EventMessage = eventMessage;
    }

    private String EventMessage;

    public TextArea chatTextArea;
    public TextArea messageTextArea;
    User user;
    public void initData(User user) {
        this.user=user;
        ExecutorService executorService = Executors.newSingleThreadExecutor();
            executorService.execute(() -> {
                try {
                    while (true) {
                        System.out.println("Initialize event source");

                        String url = "http://localhost:8080/chatnew/msgs";
                        EventSource.Builder builder = new EventSource.Builder(new EventHandler() {
                            @Override
                            public void onOpen() throws Exception {

                            }

                            @Override
                            public void onClosed() throws Exception {

                            }

                            @Override
                            public void onMessage(String s, MessageEvent messageEvent) throws Exception {
                                //передать из потока в javaFX
                                Platform.runLater(() -> {
                                    chatTextArea.appendText(messageEvent.getData());
                                    chatTextArea.appendText("\n");
                                });
                            }

                            @Override
                            public void onComment(String s) throws Exception {

                            }

                            @Override
                            public void onError(Throwable throwable) {

                            }
                        }, URI.create(url));

                        try (EventSource eventSource = builder.build()) {
                            eventSource.start();
                            TimeUnit.MINUTES.sleep(1);
                        }
                    }
                } catch (InterruptedException ignored) {}
            });
    }


    public void sendButton(ActionEvent actionEvent) throws IOException {
        message =messageTextArea.getText();
        Msg msg = new Msg(message,user);
        msgRepository.add(msg,user);

    }
}
