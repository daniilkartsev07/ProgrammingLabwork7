package ru.itmo.prog.lab5.server.commands;

import ru.itmo.prog.lab5.common.models.Dragon;
import ru.itmo.prog.lab5.server.managers.CollectionManager;
import ru.itmo.prog.lab5.common.network.Request;
import ru.itmo.prog.lab5.common.network.Response;

import java.util.Comparator;
import java.util.stream.Collectors;

/**
 * Команда 'show'. Возвращает строковое представление всех элементов коллекции.
 */
public class Show implements Command {
    private final CollectionManager collectionManager;

    public Show(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    @Override
    public Response execute(Request request) {
        if (request.getArgument() != null && !request.getArgument().isEmpty()) {
            return new Response(false, "Ошибка: команда 'show' не принимает аргументы.");
        }

        if (collectionManager.getCollection().isEmpty()) {
            return new Response(true, "Коллекция пуста.");
        }

        String result = collectionManager.getCollection().values().stream()
                .sorted(Comparator.comparing(d -> d.getCoordinates().getX())).map(Dragon::toString)
                .collect(Collectors.joining("\n"));
        return new Response(true, result);
    }

    @Override
    public String getDescription() {
        return "вывести все элементы коллекции в строковом представлении";
    }
}
