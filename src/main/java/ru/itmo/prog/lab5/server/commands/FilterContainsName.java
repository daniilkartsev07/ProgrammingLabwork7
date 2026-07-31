package ru.itmo.prog.lab5.server.commands;

import ru.itmo.prog.lab5.common.models.Dragon;
import ru.itmo.prog.lab5.server.managers.CollectionManager;
import ru.itmo.prog.lab5.common.network.Request;
import ru.itmo.prog.lab5.common.network.Response;

import java.util.Map;

/**
 * Команда 'filter_contains_name'.
 * Выводит элементы, значение поля name которых содержит заданную подстроку.
 */
public class FilterContainsName implements Command {
    private final CollectionManager collectionManager;

    public FilterContainsName(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    /**
     * Выполняет фильтрацию по подстроке имени на стороне сервера.
     * @param request Объект запроса, содержащий искомую подстроку в качестве аргумента.
     * @return Объект ответа (Response) со списком найденных элементов или сообщением об ошибке.
     */
    @Override
    public Response execute(Request request) {
        try {
            String argument = request.getArgument();
            if (argument == null || argument.isEmpty()) {
                return new Response(false, "Ошибка: введите подстроку для поиска после команды 'filter_contains_name'.");
            }
            Map<Integer, Dragon> snapshot = collectionManager.getCollection();
            if (snapshot.isEmpty()) {
                return new Response(true, "Коллекция пуста, искать не среди чего.");
            }

            StringBuilder sb = new StringBuilder();
            boolean found = false;
            sb.append("Результаты фильтрации по подстроке \"").append(argument).append("\" ===\n");


            for (Dragon dragon : snapshot.values()) {
                if (dragon.getName() != null && dragon.getName().contains(argument)) {
                    sb.append(dragon.toString()).append("\n-----------------------\n");
                    found = true;
                }
            }
            if (!found) {
                return new Response(true, "Драконов, содержащих подстроку \"" + argument + "\" в имени, не найдено.");
            }
            return new Response(true, sb.toString());
        } catch (Exception e) {
            return new Response(false, "Внутренняя ошибка сервера при фильтрации: " + e.getMessage());
        }
    }

    @Override
    public String getDescription() {
        return "вывести элементы, значение поля name которых содержит заданную подстроку";
    }
}