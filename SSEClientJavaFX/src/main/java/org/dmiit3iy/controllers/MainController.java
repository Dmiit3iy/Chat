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


public class MainController {
    private String login;
    public TextField loginTextField;
    ObjectMapper objectMapper = new ObjectMapper();

    public void chatButton(ActionEvent actionEvent) {
        login = this.loginTextField.getText();

        UserRepository userRepository = new UserRepository();
        String str = Constans.SERVER_URL + "/users?login=" + login;
        String resp;
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(userRepository.getData(str, "GET")))) {
            while (bufferedReader.ready()) {
                try {
                    resp = bufferedReader.readLine();
                    System.out.println(resp);
                    String resp2 = resp.substring(8, resp.length() - 1);
                   System.out.println(resp2);
                   User user = objectMapper.readValue(resp2, User.class);

                   if (user != null) {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/dmiit3iy/chat.fxml"));
                        Stage stage = new Stage(StageStyle.DECORATED);
                        stage.setScene(new Scene(loader.load()));
                        ChatController chatController= loader.getController();
                        chatController.initData(user);

                        loginTextField.clear();


                        stage.show();
                    }

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
