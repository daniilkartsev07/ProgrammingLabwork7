package ru.itmo.prog.lab5.server.commands;

import ru.itmo.prog.lab5.common.network.Request;
import ru.itmo.prog.lab5.common.network.Response;

/**
 * Интерфейс для реализации паттерна Command на стороне сервера.
 */
public interface Command {

    /**
     * Метод для выполнения команды на сервере.
     * @param request Объект запроса, содержащий имя команды, аргумент и (если необходимо) объект Dragon.
     * @return Объект ответа Response со статусом успеха и текстовым сообщением.
     */
    Response execute(Request request);

    /**
     * @return Описание команды для вывода в help.
     */
    String getDescription();
}
