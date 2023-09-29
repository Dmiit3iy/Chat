package org.dmiit3iy.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import org.dmiit3iy.App;
import org.dmiit3iy.model.User;
import org.dmiit3iy.repositories.UserRepository;

import java.io.IOException;

public class RegistrationController {
    UserRepository userRepository = new UserRepository();
    private String login;
    private String password;
    @FXML
    public TextField loginField;
    @FXML
    public TextField passwordField;

    @FXML
    public void registrationButton(ActionEvent actionEvent) throws IOException {
        login = loginField.getText();
        password = passwordField.getText();

        userRepository.add(new User(login,password));

    }
}
