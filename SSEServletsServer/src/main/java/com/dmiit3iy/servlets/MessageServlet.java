package com.dmiit3iy.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.dmiit3iy.DAO.DAO;
import com.dmiit3iy.dto.Event;
import com.dmiit3iy.dto.ResponseResult;
import com.dmiit3iy.model.Msg;
import com.dmiit3iy.model.User;
import com.dmiit3iy.repository.SSEEmittersRepository;
import com.dmiit3iy.service.ChatService;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import javax.servlet.AsyncContext;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.time.format.DateTimeFormatter;
import java.util.List;

@WebServlet(value = "/chatnew/msgs", asyncSupported = true)
public class MessageServlet extends HttpServlet {
    private SSEEmittersRepository emitters = new SSEEmittersRepository();
    private ChatService service;
    private   Msg msg;
    private ObjectMapper objectMapper = new ObjectMapper();

    {
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    @Override
    public void init() {
        this.service = new ChatService(this.emitters);
    }

    @Override
    public void destroy() {
        this.service.stop();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (req.getHeader("Accept").equals("text/event-stream")) {

            resp.setContentType("text/event-stream");
            resp.setHeader("Connection", "keep-alive");
            resp.setCharacterEncoding("UTF-8");

            AsyncContext asyncContext = req.startAsync();
            asyncContext.setTimeout(60000L);
            System.out.println("Подключение эмиттера"+asyncContext);
            String idUser = req.getParameter("id");
            System.out.println("Пользователь"+idUser);
            this.emitters.add(asyncContext,idUser);
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


        String s = req.getParameter("id");
        try (BufferedReader reader = req.getReader()) {
            msg = objectMapper.readValue(reader, Msg.class);

            User user = (User) DAO.getObjectById(Long.valueOf(s), User.class);
            DAO.closeOpenedSession();
            msg.setUser(user);
            DAO.addObject(msg);
            this.objectMapper.writeValue(resp.getWriter(), new ResponseResult<>(null, msg));

        //   service.addEvent(new Event(msg.getUser().getLogin(),msg.getMessage()));
        } catch (IllegalArgumentException e) {
            resp.setStatus(400);
            this.objectMapper.writeValue(resp.getWriter(), new ResponseResult<>(e.getMessage(), null));
        } catch (Exception e) {
            resp.setStatus(400);
            resp.getWriter().println("Error " + e.getMessage());
        }finally {
            DAO.closeOpenedSession();
        }
        service.addEvent(new Event(msg.getUser().getLogin(),msg.getMessage()+" "+msg.getLocalDateTime().format(DateTimeFormatter.ofPattern("yyy-MM-dd HH-mm-ss"))));
       
    }








}
