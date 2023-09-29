package org.dmiit3iy.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.dmiit3iy.model.User;
import org.dmiit3iy.repositories.UserRepository;
import org.dmiit3iy.utils.Constans;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.prefs.Preferences;


public class MainController {
    public TextField passwordTextArea;
    private String login;
    private String password;
    public TextField loginTextField;
    ObjectMapper objectMapper = new ObjectMapper();

    public void chatButton(ActionEvent actionEvent) throws IOException {
        login = this.loginTextField.getText();
        password= this.passwordTextArea.getText();
        UserRepository userRepository = new UserRepository();
        User user =userRepository.getUser(login,password);
        if(user!=null){
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/dmiit3iy/chat.fxml"));
            Stage stage = new Stage(StageStyle.DECORATED);
            stage.setScene(new Scene(loader.load()));

            ChatController chatController = loader.getController();
            chatController.initData(user);
            Preferences userlog = Preferences.userRoot();
            Preferences userIDlog = Preferences.userRoot();
            String userID = String.valueOf(user.getId());
            userlog.putBoolean("authorization", true);
            userIDlog.put("userID", userID);


            stage.setOnCloseRequest(chatController.getCloseEventHandler());
            loginTextField.clear();
            passwordTextArea.clear();
            stage.show();
        }

    }


    public void registraionButton(ActionEvent actionEvent) throws IOException {

        Stage stage1 = (Stage) loginTextField.getScene().getWindow();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/dmiit3iy/registration.fxml"));
        Stage stage = new Stage(StageStyle.DECORATED);
        stage.setScene(new Scene(loader.load()));
        stage.show();
    }
}
