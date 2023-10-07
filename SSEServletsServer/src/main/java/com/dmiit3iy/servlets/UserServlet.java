package com.dmiit3iy.servlets;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.dmiit3iy.DAO.DAO;
import com.dmiit3iy.dto.ResponseResult;
import com.dmiit3iy.model.User;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;

@WebServlet("/chatnew/users")
public class UserServlet extends HttpServlet {
    private ObjectMapper objectMapper = new ObjectMapper();



    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json;charset=utf-8");

        try (BufferedReader reader = req.getReader()) {
            User user = objectMapper.readValue(reader, User.class);
            DAO.addObject(user);
            this.objectMapper.writeValue(resp.getWriter(), new ResponseResult<>(null, user));
        } catch (IllegalArgumentException e) {
            resp.setStatus(400);
            this.objectMapper.writeValue(resp.getWriter(), new ResponseResult<>(e.getMessage(), null));
        } catch (Exception e) {
            resp.setStatus(400);
            resp.getWriter().println("Error " + e.getMessage());
        }
    }

    /**
     * @param req
     * @param resp
     * @throws IOException
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json;charset=utf-8");
        String id = req.getParameter("id");
        String login = req.getParameter("login");
        String password = req.getParameter("password");
        if (id != null&login==null&password==null) {
            try {
                User user = (User) DAO.getObjectById(Long.valueOf(id), User.class);
                if (user == null) {
                    resp.setStatus(400);
                    this.objectMapper.writeValue(resp.getWriter(), new ResponseResult<>
                            ("Пользователь отсутствует в базе данных", null));

                } else {
                    this.objectMapper.writeValue(resp.getWriter(), new ResponseResult<>(null, user));
                }
                DAO.closeOpenedSession();
            } catch (NumberFormatException e) {
                resp.setStatus(400);
                this.objectMapper.writeValue(resp.getWriter(), new ResponseResult<>(e.getMessage(), null));
            }
        }
        if (id == null&login!=null&password!=null) {
            String[] param = {login,password};
            String[] param2 ={"login","password"};
            User user = (User) DAO.getObjectByParams(param2,param, User.class);
            if (user == null) {
                resp.setStatus(400);
                this.objectMapper.writeValue(resp.getWriter(), new ResponseResult<>
                        ("Введены неверные данные или пользователь отсутствует в базе данных", null));
            } else {
                this.objectMapper.writeValue(resp.getWriter(), new ResponseResult<>(null, user));
            }
            DAO.closeOpenedSession();
        }
    }

    /**
     * осуществляет удаление пользователя с заданным id из базы данных,
     * а так же каскадное удаление всей информации, связанной с ним
     *
     * @param req
     * @param resp
     * @throws IOException
     */
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json;charset=utf-8");
        String userId = req.getParameter("userId");
        if (userId != null) {
            try {
                User user = (User) DAO.getObjectById(Long.valueOf(userId), User.class);
                DAO.closeOpenedSession();
                if (user == null) {
                    resp.setStatus(400);
                    this.objectMapper.writeValue(resp.getWriter(), new ResponseResult<>
                            ("Пользователь отсутствует в базе данных", null));
                } else {
                    DAO.deleteObject(user);
                    this.objectMapper.writeValue(resp.getWriter(), new ResponseResult<>(null, user));
                }
            } catch (NumberFormatException e) {
                resp.setStatus(400);
                this.objectMapper.writeValue(resp.getWriter(), new ResponseResult<>
                        (e.getMessage(), null));
            }
        }
    }
}
