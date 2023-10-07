package org.dmiit3iy.repositories;


import com.fasterxml.jackson.core.type.TypeReference;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import javafx.scene.control.Alert;
import org.dmiit3iy.App;
import org.dmiit3iy.dto.ResponseResult;
import org.dmiit3iy.model.Msg;
import org.dmiit3iy.model.User;
import org.dmiit3iy.utils.Constans;


import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;


public class MsgRepository {
    private ObjectMapper objectMapper = new ObjectMapper();

    {
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    public  <T> InputStream getData(String link, String method, T value) throws IOException {
        URL url = new URL(link);
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        httpURLConnection.setRequestMethod(method);
        httpURLConnection.setRequestProperty("Content-Type", "application/json;utf-8");
        httpURLConnection.setDoOutput(true);
        try (BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(httpURLConnection.getOutputStream())) {
            this.objectMapper.writeValue(bufferedOutputStream, value);
            if (httpURLConnection.getResponseCode() == 400) {
                ResponseResult<Msg> responseResult = objectMapper.readValue(new InputStreamReader
                        (httpURLConnection.getErrorStream()), new TypeReference<ResponseResult<Msg>>() {
                });
                App.showMessage("ошибка", responseResult.getMessage(), Alert.AlertType.ERROR);
                throw new IllegalArgumentException(responseResult.getMessage());
            }
        }
        return httpURLConnection.getInputStream();
    }
    public InputStream getData(String link, String method) throws IOException {
        URL url = new URL(link);
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        httpURLConnection.setRequestMethod(method);
        if (httpURLConnection.getResponseCode() == 400) {
            ResponseResult<Msg> responseResult = objectMapper.readValue(new InputStreamReader
                    (httpURLConnection.getErrorStream()), new TypeReference<ResponseResult<Msg>>() {
            });
            App.showMessage("ошибка", responseResult.getMessage(), Alert.AlertType.ERROR);
            throw new IllegalArgumentException(responseResult.getMessage());
        }
        return httpURLConnection.getInputStream();
    }

    public List<Msg> get() throws IOException {
        try (InputStream inputStream = getData(Constans.SERVER_URL + "/msgs", "GET")) {

            ResponseResult<ArrayList<Msg>> responseResult = objectMapper.readValue(inputStream, new TypeReference<ResponseResult<ArrayList<Msg>>>() {});

            return responseResult.getData();
        }
    }




    public Msg add(Msg msg) throws IOException {
        try (InputStream inputStream = getData(Constans.SERVER_URL + "/msgs?userId="+msg.getUser().getId(), "POST", msg)) {
            ResponseResult<Msg> responseResult = objectMapper.readValue(inputStream, new TypeReference<ResponseResult<Msg>>() {
            });
            return responseResult.getData();
        }
    }

    public CopyOnWriteArrayList<String> getEmitters() throws IOException {
        try (InputStream inputStream = getData(Constans.SERVER_URL + "/msgs", "PUT")) {

            ResponseResult<CopyOnWriteArrayList<String>> responseResult = objectMapper.readValue(inputStream, new TypeReference<>() {});

            return responseResult.getData();
        }
    }

}
