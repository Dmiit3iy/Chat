package com.dmiit3iy.servlets;

import com.dmiit3iy.repository.SSEEmittersRepository;
import com.dmiit3iy.repository.SSEOnlineRepository;
import com.dmiit3iy.service.ChatService;
import com.dmiit3iy.service.OnlineService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import javax.servlet.AsyncContext;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(value = "/chatnew/online", asyncSupported = true)
public class OnlineServlet extends HttpServlet {
    private SSEOnlineRepository sseOnlineRepository = new SSEOnlineRepository();

    private OnlineService service;

    private ObjectMapper objectMapper = new ObjectMapper();

    {
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    @Override
    public void init() {

        this.service = new OnlineService(this.sseOnlineRepository);
    }

    @Override
    public void destroy() {
        this.service.stop();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (req.getHeader("Accept") != null && req.getHeader("Accept").equals("text/event-stream")) {

            resp.setContentType("text/event-stream");
            resp.setHeader("Connection", "keep-alive");
            resp.setCharacterEncoding("UTF-8");

            AsyncContext asyncContext = req.startAsync();
            asyncContext.setTimeout(600L);
            System.out.println("Подключение эмиттера" + asyncContext);
            sseOnlineRepository.add(asyncContext);
        }
    }
}