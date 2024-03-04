package com.tictactoe;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@WebServlet(name = "InitServlet", value ="/start")
public class InitServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession currentSession = req.getSession(true); // новая сессия

        Field field = new Field(); // игровое поле
        Map<Integer, Sign> fieldData = field.getField();

        List<Sign> data = field.getFieldData(); // получение списка значений

        currentSession.setAttribute("field", field); // добавление в сессию параметров поля
        currentSession.setAttribute("data", data); // и значений поля (сортировка по индексу)

        getServletContext().getRequestDispatcher("/index.jsp").forward(req, resp);
        // перенаправление запроса на страницу index.jsp через сервер
    }
}
