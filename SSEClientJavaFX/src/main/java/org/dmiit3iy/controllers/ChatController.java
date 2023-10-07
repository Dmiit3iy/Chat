package org.dmiit3iy.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
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

import org.dmiit3iy.dto.Event;
import org.dmiit3iy.model.Msg;
import org.dmiit3iy.model.User;

import org.dmiit3iy.repositories.MsgRepository;
import org.dmiit3iy.repositories.UserRepository;
import org.dmiit3iy.utils.EventWrapper;


import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.*;
import java.util.prefs.Preferences;
import java.util.stream.Collectors;

public class ChatController {
       private ObjectMapper objectMapper = new ObjectMapper();

    {
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    public Button logOfButtonId;
    public ListView onLineUsersListView;
    public Label loginLable;
    ArrayList<String> emittersList = new ArrayList<>();
    private BlockingQueue<String> blockingQueue = new LinkedBlockingQueue<>();

    MsgRepository msgRepository = new MsgRepository();

    private String message;


    private String EventMessage;
    ExecutorService executorService = Executors.newSingleThreadExecutor();


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

                    String url = "http://localhost:8080/chatnew/msgs" + "?userId=" + user.getId();
                    String url2 = "http://localhost:8080/chatnew/online";

                    EventSource.Builder builder = new EventSource.Builder(new EventHandler() {
                        @Override
                        public void onOpen() throws Exception {
                        }

                        @Override
                        public void onClosed() throws Exception {
                        }

                        @Override
                        public void onMessage(String s, MessageEvent messageEvent) throws Exception {

                            Platform.runLater(() -> {
                                System.out.println(messageEvent.getData());

                                try {
                                    Event event = objectMapper.readValue(messageEvent.getData(), new TypeReference<Event>() {
                                    });
                                    Msg msg = objectMapper.readValue(event.getMessage(), Msg.class);

                                    chatTextArea.appendText(EventWrapper.makeString(msg));
                                    chatTextArea.appendText("\n");
                                } catch (JsonProcessingException e) {
                                    throw new RuntimeException(e);
                                }
                            });
                        }

                        @Override
                        public void onComment(String s) throws Exception {
                        }

                        @Override
                        public void onError(Throwable throwable) {
                        }
                    }, URI.create(url));

                    EventSource.Builder builder2 = new EventSource.Builder(new EventHandler() {
                        @Override
                        public void onOpen() throws Exception {
                        }

                        @Override
                        public void onClosed() throws Exception {
                        }

                        @Override
                        public void onMessage(String s, MessageEvent messageEvent) throws Exception {

                            Platform.runLater(() -> {
                                System.out.println(messageEvent.getData());

                                try {
                                    Event event = objectMapper.readValue(messageEvent.getData(), new TypeReference<Event>() {

                                    });

                                    System.out.println(event+"Это ивент!!!");

                                    ArrayList<User> userArrayList = objectMapper.readValue(event.getMessage(), new TypeReference<ArrayList<User>>() {
                                    });

                                 emittersList = (ArrayList<String>) userArrayList.stream().map(x->x.getLogin()).collect(Collectors.toList());

                                   ObservableList<String> userList = FXCollections.observableList(emittersList);
                                   onLineUsersListView.setItems(userList);
                                } catch (JsonProcessingException e) {
                                    throw new RuntimeException(e);
                                }
                            });
                        }

                        @Override
                        public void onComment(String s) throws Exception {
                        }

                        @Override
                        public void onError(Throwable throwable) {
                        }
                    }, URI.create(url2));


                    try (EventSource eventSource = builder.build();
                         EventSource eventSource2 = builder2.build()) {

                        eventSource.start();
                        eventSource2.start();

                        TimeUnit.SECONDS.sleep(10);
                    }
                }
            } catch (InterruptedException ignored) {
            }
        });
    }


    public void sendButton(ActionEvent actionEvent) throws IOException {
        message = messageTextArea.getText();
        Msg msg = new Msg(message, user);
        msgRepository.add(msg);
        messageTextArea.clear();

    }

    public void LogOffButton(ActionEvent actionEvent) {
        Preferences userlog = Preferences.userRoot();
        userlog.put("authorization", "null");
        Preferences userIDlog = Preferences.userRoot();
        userlog.putBoolean("authorization", false);
        userIDlog.put("userID", "-1");
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


    public void getAllMsgButton(ActionEvent actionEvent) throws IOException {
        chatTextArea.clear();
        List<Msg> msgArrayList = msgRepository.get();
        msgArrayList.sort(new Comparator<Msg>() {
            @Override
            public int compare(Msg o1, Msg o2) {
                return o1.getLocalDateTime().compareTo(o2.getLocalDateTime());
            }
        });
        for (Msg m : msgArrayList
        ) {
            chatTextArea.appendText(EventWrapper.makeString(m));
            chatTextArea.appendText("\n");
        }

    }
}
