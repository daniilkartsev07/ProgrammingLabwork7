package ru.itmo.prog.lab5.server.commands;

import ru.itmo.prog.lab5.server.managers.CollectionManager;
import ru.itmo.prog.lab5.common.models.Dragon;
import ru.itmo.prog.lab5.common.network.Request;
import ru.itmo.prog.lab5.common.network.Response;

import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Команда 'print_field_ascending_color'.
 * Выводит значения поля color всех элементов в порядке возрастания.
 */
public class PrintFieldAscendingColor implements Command {
    private final CollectionManager collectionManager;

    public PrintFieldAscendingColor(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    /**
     * Выполняет сортировку и вывод цветов на стороне сервера.
     * @param request Объект запроса от клиента.
     * @return Объект ответа (Response) со списком отсортированных цветов.
     */
    @Override
    public Response execute(Request request) {
            String argument = request.getArgument();

            if (argument != null && !argument.isEmpty()) {
                return new Response(false, "Ошибка: команда 'print_field_ascending_color' не принимает аргументы.");
            }
            if (collectionManager.getCollection().isEmpty()) {
                return new Response(true, "Коллекция пуста, цветов нет.");
            }
            String result = collectionManager.printFieldAscendingColor();
            return new Response(true, result);

    }

    @Override
    public String getDescription() {
        return "вывести значения поля color всех элементов в порядке возрастания";
    }
}
