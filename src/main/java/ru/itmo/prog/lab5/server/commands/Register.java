package ru.itmo.prog.lab5.server.commands;

import ru.itmo.prog.lab5.common.models.User;
import ru.itmo.prog.lab5.common.network.Request;
import ru.itmo.prog.lab5.common.network.Response;
import ru.itmo.prog.lab5.server.managers.UserDAO;

import java.sql.SQLException;

public class Register implements Command {
    private final UserDAO userDAO;

    public Register(UserDAO userDAO) {
        this.userDAO = userDAO;
    }
    @Override
    public Response execute(Request request) {
        String login = request.getLogin();
        String password = request.getPassword();
        if (login == null || login.isEmpty() || password == null || password.isEmpty()) {
            return new Response(false, "Ошибка: для регистрации укажите логин и пароль.");
        }
        try {
            User user = userDAO.register(login, password);
            if (user == null) {
                return new Response(false, "Ошибка: '" + login + "' уже занят.");
            }
            return new Response(true, "Регистрация прошла успешно. Добро пожаловать, " + login + "!");
        } catch (SQLException e) {
            return new Response(false, "Ошибка БД при регистрации" + e.getMessage());
        }
    }
    @Override
    public String getDescription() {
        return "Зарегистрировать нового пользователя (логин и пароль)";
    }
}
