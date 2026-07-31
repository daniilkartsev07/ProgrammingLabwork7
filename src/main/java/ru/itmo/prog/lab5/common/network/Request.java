package ru.itmo.prog.lab5.common.network;

import ru.itmo.prog.lab5.common.models.Dragon;
import ru.itmo.prog.lab5.common.models.User;

import java.io.Serializable;

/**
 * Упаковывает команду пользователя и все необходимые данные для отправки от клиента к серверу.
 */

public class Request implements Serializable {
    private static final long serialVersionUID = 1L;
    private final String commandName;
    private final String argument;
    private final Dragon dragon;
    private final String login;
    private final String password;
    private transient User authenticatedUser;

    public Request(String commandName, String argument, String login, String password) {
        this(commandName, argument, null, login, password);
    }

    public Request(String commandName, String argument, Dragon dragon, String login, String password) {
        this.dragon = dragon;
        this.argument = argument;
        this.commandName = commandName;
        this.password = password;
        this.login = login;
    }
    public void setAuthenticatedUser(User authenticatedUser) {
        this.authenticatedUser = authenticatedUser;
    }
    public User getAuthenticatedUser() {
        return authenticatedUser;
    }

    public String getCommandName() {
        return commandName;
    }

    public String getArgument() {
        return argument;
    }

    public Dragon getDragon() {
        return dragon;
    }
    public boolean isEmpty() {
        return commandName.isEmpty() && argument.isEmpty() && dragon == null;
    }
    public String getLogin() {
        return login;
    }
    public String getPassword() {
        return password;
    }
}
