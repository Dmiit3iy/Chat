package com.dmiit3iy.servlets;

import com.dmiit3iy.DAO.DAO;
import com.dmiit3iy.dto.ResponseResult;
import com.dmiit3iy.model.Msg;
import com.dmiit3iy.repository.SSEEmittersRepository;
import com.dmiit3iy.repository.SSEOnlineEmittorsRepository;
import com.dmiit3iy.service.ChatService;
import com.dmiit3iy.service.EmittersService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import javax.servlet.AsyncContext;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet(value = "/chatnew/emiters", asyncSupported = true)
public class EmittersServlet extends HttpServlet {

    private SSEEmittersRepository emitters = new SSEEmittersRepository();
    private SSEOnlineEmittorsRepository emittorsRepository =new SSEOnlineEmittorsRepository();



    private EmittersService service;
    private Msg msg;
    private ObjectMapper objectMapper = new ObjectMapper();

    {
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    @Override
    public void init() {
        this.service = new EmittersService(this.emittorsRepository);
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
            System.out.println("Подключение эмиттера" + asyncContext);


            this.emittorsRepository.add(asyncContext);
            //  this.objectMapper.writeValue(resp.getWriter(), new ResponseResult<>(null, emitters.getOnlineEmitters()));

        }

    }
}
