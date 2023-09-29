package org.dmiit3iy.controllers;

import com.launchdarkly.eventsource.EventHandler;
import com.launchdarkly.eventsource.EventSource;
import com.launchdarkly.eventsource.MessageEvent;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.dmiit3iy.model.Msg;
import org.dmiit3iy.model.User;
import org.dmiit3iy.repositories.MsgRepository;
import org.dmiit3iy.utils.SimpleEventHandler;

import java.io.IOException;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.concurrent.*;
import java.util.prefs.Preferences;

public class ChatController {

    public Button logOfButtonId;
    public ListView onLineUsersListView;
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
    ExecutorService executorService = Executors.newSingleThreadExecutor();

    public TextArea chatTextArea;
    public TextArea messageTextArea;
    User user;
    public void initData(User user) {
        this.user=user;

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
        LocalDateTime localDateTime = LocalDateTime.now();
        Msg msg = new Msg(message,localDateTime,user);
        msgRepository.add(msg,user);

    }

    public void LogOffButton(ActionEvent actionEvent) {
        Preferences userlog = Preferences.userRoot();
        userlog.put("authorization", "null");
        Preferences userIDlog = Preferences.userRoot();
        userlog.putBoolean("authorization", false);
        userIDlog.put("userID","-1");
        executorService.shutdownNow();
        Stage stage1 = (Stage) logOfButtonId.getScene().getWindow();
        stage1.close();
    }

    private javafx.event.EventHandler<WindowEvent> closeEventHandler = new javafx.event.EventHandler<WindowEvent>() {
        @Override
        public void handle(WindowEvent event) {
           executorService.shutdownNow();
        }
    };

    public javafx.event.EventHandler<WindowEvent> getCloseEventHandler() {
        return closeEventHandler;
    }


}
