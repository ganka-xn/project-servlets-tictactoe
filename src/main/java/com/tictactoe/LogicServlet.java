package com.tictactoe;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

@WebServlet(name = "LogicServlet", value = "/logic")
public class LogicServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        HttpSession currentSession = req.getSession();
        Field field = extractField(currentSession);

        int index = getSelectedIndex(req); // получаем индекс ячейки, по которой произошел клик
        Sign currentSign = field.getField().get(index);

        // Проверяем, что ячейка, по которой был клик пустая.
        // Иначе ничего не делаем и отправляем пользователя на ту же страницу без изменений
        // параметров в сессии
        if (Sign.EMPTY != currentSign) {
            RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/index.jsp");
            dispatcher.forward(req, resp);
            return;
        }

        field.getField().put(index, Sign.CROSS); // ставим крестик в ячейке, по которой кликнул пользователь

        // не победил ли крестик?
        if (checkWin(resp, currentSession, field)) {
            return;
        }

        // получаем пустую ячейку поля
        int emptyFieldIndex = field.getEmptyFieldIndex();
        if (emptyFieldIndex >= 0) {
            field.getField().put(emptyFieldIndex, Sign.NOUGHT);
            // не победил ли нолик?
            if (checkWin(resp, currentSession, field)) {
                return;
            }
        }
        else {
            currentSession.setAttribute("draw", true); // флаг, который сигнализирует что произошла ничья

            List<Sign> data = field.getFieldData(); // Считаем список значков
            currentSession.setAttribute("data", data); // Обновляем этот список в сессии

            resp.sendRedirect("/index.jsp");
            return;
        }

        List<Sign> data = field.getFieldData(); // считаем список значков

        currentSession.setAttribute("data", data); // Обновляем объект поля и список значков в сессии
        currentSession.setAttribute("field", field);

        resp.sendRedirect("/index.jsp");
    }

    private int getSelectedIndex(HttpServletRequest req) {
        String click = req.getParameter("click");
        boolean isNumeric = click.chars().allMatch(Character::isDigit);
        return isNumeric
            ? Integer.parseInt(click)
            : 0;
    }

    private Field extractField(HttpSession currentSession) {
        Object fieldAttribute = currentSession.getAttribute("field");
        if (Field.class != fieldAttribute.getClass()) {
            currentSession.invalidate();
            throw new RuntimeException("Session is broken, try one more time");
        }
        return (Field) fieldAttribute;
    }

    private boolean checkWin(HttpServletResponse response, HttpSession currentSession, Field field) throws IOException {
        Sign winner = field.checkWin();
        if (Sign.CROSS == winner || Sign.NOUGHT == winner) {
            currentSession.setAttribute("winner", winner); // флаг, который показывает что кто-то победил
            List<Sign> data = field.getFieldData();
            currentSession.setAttribute("data", data);

            response.sendRedirect("/index.jsp");
            return true;
        }
        return false;
    }
}
