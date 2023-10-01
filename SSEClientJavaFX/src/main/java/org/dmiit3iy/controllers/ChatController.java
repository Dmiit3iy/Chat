package org.dmiit3iy.controllers;

import com.launchdarkly.eventsource.EventHandler;
import com.launchdarkly.eventsource.EventSource;
import com.launchdarkly.eventsource.MessageEvent;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import org.dmiit3iy.model.Msg;
import org.dmiit3iy.model.User;

import org.dmiit3iy.repositories.MsgRepository;
import org.dmiit3iy.utils.EventWrapper;


import java.io.IOException;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.*;
import java.util.prefs.Preferences;

public class ChatController {

    public Button logOfButtonId;
    public ListView onLineUsersListView;
    public Label loginLable;
    CopyOnWriteArrayList<String> emittersList = new CopyOnWriteArrayList<>();
    private BlockingQueue<String> blockingQueue = new LinkedBlockingQueue<>();

    public void setBlockingQueue(String s) throws InterruptedException {
        this.blockingQueue.put(s);
    }

    MsgRepository msgRepository = new MsgRepository();

    private String message;



    private String EventMessage;
    ExecutorService executorService = Executors.newSingleThreadExecutor();

    ExecutorService executorService2 = Executors.newSingleThreadExecutor();

    public TextArea chatTextArea;
    public TextArea messageTextArea;
    User user;

    public void initData(User user) {
        loginLable.setText(user.getLogin());
        chatTextArea.setEditable(false);
        this.user = user;

        executorService.execute(() -> {
            try {
                while (true) {
                    System.out.println("Initialize event source");

                    String url = "http://localhost:8080/chatnew/msgs" + "?login=" + user.getLogin();
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
                                System.out.println(messageEvent.getData());
                                chatTextArea.appendText(EventWrapper.makeString(messageEvent));
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

            } catch (InterruptedException ignored) {
            }
        });

        executorService2.execute(() -> {
            try {while (true) {
                ObservableList<String> listEmitters = FXCollections.observableArrayList(msgRepository.getEmitters());
                onLineUsersListView.setItems(listEmitters);
                Thread.sleep(1000);
            }
            } catch (IOException | InterruptedException ignored) {

            }
        });
    }


    public void sendButton(ActionEvent actionEvent) throws IOException {
        message = messageTextArea.getText();
        LocalDateTime localDateTime = LocalDateTime.now();
        Msg msg = new Msg(message, localDateTime, user);
        msgRepository.add(msg, user);
        messageTextArea.clear();

    }

    public void LogOffButton(ActionEvent actionEvent) {
        Preferences userlog = Preferences.userRoot();
        userlog.put("authorization", "null");
        Preferences userIDlog = Preferences.userRoot();
        userlog.putBoolean("authorization", false);
        userIDlog.put("userID", "-1");
        executorService.shutdownNow();
        executorService2.shutdownNow();
        Stage stage1 = (Stage) logOfButtonId.getScene().getWindow();
        stage1.close();
    }

    private javafx.event.EventHandler<WindowEvent> closeEventHandler = new javafx.event.EventHandler<WindowEvent>() {
        @Override
        public void handle(WindowEvent event) {
            executorService.shutdownNow();
            executorService2.shutdownNow();
        }
    };

    public javafx.event.EventHandler<WindowEvent> getCloseEventHandler() {
        return closeEventHandler;
    }


    public void getAllMsgButton(ActionEvent actionEvent) throws IOException {
        List<Msg> msgArrayList = msgRepository.get();
        msgArrayList.sort(new Comparator<Msg>() {
            @Override
            public int compare(Msg o1, Msg o2) {
                return o1.getLocalDateTime().compareTo(o2.getLocalDateTime());
            }
        });
        for (Msg m : msgArrayList
        ) {
            chatTextArea.appendText(m.toString());
            chatTextArea.appendText("\n");
        }

    }
}
