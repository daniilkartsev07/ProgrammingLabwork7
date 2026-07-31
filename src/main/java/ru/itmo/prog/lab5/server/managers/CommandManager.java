package ru.itmo.prog.lab5.server.managers;

import ru.itmo.prog.lab5.common.models.User;
import ru.itmo.prog.lab5.server.commands.Command;
import ru.itmo.prog.lab5.common.network.Request;
import ru.itmo.prog.lab5.common.network.Response;

import java.sql.SQLException;
import java.util.Map;
import java.util.HashMap;
import java.util.Collections;
import java.util.Set;

public class CommandManager {
    private final Map<String, Command> commands = new HashMap<>();
    private final UserDAO userDAO;

    private static final Set<String> PUBLIC_COMMANDS = Set.of("register");

    public CommandManager(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    /**
     * Добавляет команду в реестр.
     */
    public void register(String commandName, Command command) {
        commands.put(commandName, command);
    }

    /**
     * Находит и выполняет команду по имени, переданному в запросе.
     */
    public Response execute(Request request) {
        String commandName = request.getCommandName();
        Command command = commands.get(request.getCommandName());
        if (command == null) {
            return new Response(false, "Команда '" + commandName + "' не найдена. Введите 'help'.");
        }
        if (!PUBLIC_COMMANDS.contains(commandName)) {
            String login = request.getLogin();
            String password = request.getPassword();
            if (login == null || login.isEmpty() || password == null || password.isEmpty()) {
                return new Response(false, "Ошибка: эта команда требует авторизации. Выполните 'register', если еще не зарегистрированы.");
            }
            User user;
            try {
                user = userDAO.authenticate(login, password);
            } catch (SQLException e) {
                return new Response(false, "Ошибка проверки пользователя: " + e.getMessage());
            }
            if (user == null) {
                return new Response(false, "Ошибка: неверный логин или пароль.");
            }
            request.setAuthenticatedUser(user);
        }
        return command.execute(request);
    }


    /**
     * @return Неизменяемый словарь всех зарегистрированных команд для вывода в help.
     */
    public Map<String, Command> getCommands() {
        return Collections.unmodifiableMap(commands);
    }
}
