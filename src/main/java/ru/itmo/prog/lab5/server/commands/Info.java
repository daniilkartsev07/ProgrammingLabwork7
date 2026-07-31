package ru.itmo.prog.lab5.server.commands;

import ru.itmo.prog.lab5.server.managers.CollectionManager;
import ru.itmo.prog.lab5.common.network.Request;
import ru.itmo.prog.lab5.common.network.Response;

/**
 * Команда 'info'. Выводит информацию о коллекции.
 */
public class Info implements Command {
    private final CollectionManager collectionManager;

    public Info(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    @Override
    public Response execute(Request request) {
        String argument = request.getArgument();
        if (argument != null && !argument.isEmpty()) {
            return new Response(false, "Ошибка: команда 'info' не принимает аргументы.");
        }
        return new Response(true, collectionManager.getInfo());
    }

    @Override
    public String getDescription() {
        return "вывести в стандартный поток вывода информацию о коллекции";
    }
}
