package org.dmiit3iy.repositories;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.scene.control.Alert;
import org.dmiit3iy.App;
import org.dmiit3iy.dto.ResponseResult;
import org.dmiit3iy.model.User;
import org.dmiit3iy.utils.Constans;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;


public class UserRepository {
    ObjectMapper objectMapper = new ObjectMapper();

    public <T> InputStream getData(String link, String method, T value) throws IOException {
        URL url = new URL(link);
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        httpURLConnection.setRequestMethod(method);
        httpURLConnection.setRequestProperty("Content-Type", "application/json;utf-8");
        httpURLConnection.setDoOutput(true);
        try (BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(httpURLConnection.getOutputStream())) {
            this.objectMapper.writeValue(bufferedOutputStream, value);
            if (httpURLConnection.getResponseCode() == 400) {
                try (BufferedReader bufferedReader = new BufferedReader(
                        new InputStreamReader(httpURLConnection.getErrorStream()))) {
                    String error = bufferedReader.readLine();
                    throw new IllegalArgumentException(error);
                }
            }
        }
        return httpURLConnection.getInputStream();
    }

    public InputStream getData(String link, String method) throws IOException {
        URL url = new URL(link);
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        httpURLConnection.setRequestMethod(method);
        if (httpURLConnection.getResponseCode() == 400) {
            try (BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(httpURLConnection.getErrorStream()))) {
                String error = bufferedReader.readLine();
                App.showMessage("ошибка", error.substring(12, error.length() - 2), Alert.AlertType.ERROR);
                throw new IllegalArgumentException(error);
            }
        }
        return httpURLConnection.getInputStream();
    }

    public ArrayList<User> get() throws IOException {
        try (InputStream inputStream = getData(Constans.SERVER_URL + "/users", "GET")) {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(inputStream, new TypeReference<>() {
            });
        }
    }

    public User getUser(String login, String password) throws IOException {
        InputStream inputStream = getData(Constans.SERVER_URL + "/users?login=" + login+"&password="+password, "GET");
        ResponseResult<User> responseResult = objectMapper.readValue(inputStream, new TypeReference<ResponseResult<User>>() {
        });
        return responseResult.getData();
    }

    public User add(User user) throws IOException {
        try (InputStream inputStream = getData(Constans.SERVER_URL + "/users", "POST", user)) {
            //из inputstream приходит строка:{"data":{"id":9,"login":"qweeeee","password":""}}
            ResponseResult<User> responseResult = objectMapper.readValue(inputStream, new TypeReference<ResponseResult<User>>() {
            });
            return responseResult.getData();
        }
    }
}
