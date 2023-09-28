package org.dmiit3iy.repositories;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.scene.control.Alert;
import org.dmiit3iy.App;
import org.dmiit3iy.model.Msg;
import org.dmiit3iy.model.User;
import org.dmiit3iy.utils.Constans;


import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


public class MsgRepository {
    ObjectMapper objectMapper = new ObjectMapper();

    public  <T> InputStream getData(String link, String method, T value) throws IOException {
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
                    String error = bufferedReader.readLine();//TODO  принять объект responceresult и взять из него message
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
                App.showMessage("ошибка", error.substring(12,error.length()-2), Alert.AlertType.ERROR);
                throw new IllegalArgumentException(error);
            }
        }
        return httpURLConnection.getInputStream();
    }

    public ArrayList<User> get() throws IOException {
        try (InputStream inputStream = getData(Constans.SERVER_URL + "/msg", "GET")) {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(inputStream, new TypeReference<>() {});
        }
    }

    public Msg add(Msg msg,User user) throws IOException {
        try (InputStream inputStream = getData(Constans.SERVER_URL + "/msgs?id="+user.getId(), "POST", msg)) {
            return objectMapper.readValue(inputStream, Msg.class);
        }
    }
}
