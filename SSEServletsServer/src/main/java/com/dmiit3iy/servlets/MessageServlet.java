package com.dmiit3iy.servlets;

import com.dmiit3iy.DAO.DAO;
import com.dmiit3iy.dto.Event;
import com.dmiit3iy.dto.ResponseResult;
import com.dmiit3iy.model.Msg;
import com.dmiit3iy.model.User;
import com.dmiit3iy.repository.SSEEmittersRepository;
import com.dmiit3iy.service.ChatService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import javax.servlet.AsyncContext;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;

@WebServlet(value = "/chatnew/msgs", asyncSupported = true)
public class MessageServlet extends HttpServlet {
    private SSEEmittersRepository repository = new SSEEmittersRepository();

    private ChatService service;

    private ObjectMapper objectMapper = new ObjectMapper();

    {
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    @Override
    public void init() {

        this.service = new ChatService(this.repository);
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
            asyncContext.setTimeout(60000L);
            System.out.println("Подключение эмиттера" + asyncContext);
            long userId = Long.parseLong(req.getParameter("userId"));
            System.out.println("Пользователь" + userId);
            SSEEmittersRepository.add(asyncContext, userId);

        } else {
            resp.setCharacterEncoding("utf-8");
            req.setCharacterEncoding("utf-8");
            resp.setContentType("application/json");
            try {
                List<Msg> list = DAO.getAllObjects(Msg.class);
                DAO.closeOpenedSession();

                this.objectMapper.writeValue(resp.getWriter(), new ResponseResult<>(null, list));
            } catch (IOException e) {
                this.objectMapper.writeValue(resp.getWriter(), new ResponseResult<>(e.getMessage(), null));
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setCharacterEncoding("utf-8");
        req.setCharacterEncoding("utf-8");
        resp.setContentType("application/json;charset=utf-8");
        String s = req.getParameter("userId"); //TODO userId
        try (BufferedReader reader = req.getReader()) {
            Msg msg = objectMapper.readValue(reader, Msg.class);

            User user = (User) DAO.getObjectById(Long.valueOf(s), User.class);
            DAO.closeOpenedSession();
            msg.setUser(user);
            DAO.addObject(msg);
            this.objectMapper.writeValue(resp.getWriter(), new ResponseResult<>(null, msg));

            String jsonMessage = objectMapper.writeValueAsString(msg);
            service.addEvent(new Event(msg.getUser().getLogin(), jsonMessage));
        } catch (IllegalArgumentException e) {
            resp.setStatus(400);
            this.objectMapper.writeValue(resp.getWriter(), new ResponseResult<>(e.getMessage(), null));
        } catch (Exception e) {
            resp.setStatus(400);
            resp.getWriter().println("Error " + e.getMessage());
        }
    }
}
