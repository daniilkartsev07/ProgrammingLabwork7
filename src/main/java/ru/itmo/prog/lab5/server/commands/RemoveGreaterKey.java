package ru.itmo.prog.lab5.server.commands;

import ru.itmo.prog.lab5.common.models.User;
import ru.itmo.prog.lab5.server.managers.CollectionManager;
import ru.itmo.prog.lab5.common.network.Request;
import ru.itmo.prog.lab5.common.network.Response;

import java.sql.SQLException;

/**
 * Команда 'remove_greater_key'. Удаляет из коллекции все элементы, ключ которых больше заданного.
 */
public class RemoveGreaterKey implements Command {
    private final CollectionManager collectionManager;

    public RemoveGreaterKey(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    /**
     * Выполняет удаление элементов по ключу на стороне сервера.
     * @param request Объект запроса от клиента.
     * @return Объект ответа (Response).
     */
    @Override
    public Response execute(Request request) {
        try {
            String argument = request.getArgument();


            if (argument == null || argument.isEmpty()) {
                return new Response(false, "Ошибка: Команда 'remove_greater_key' требует числовой ключ в качестве аргумента.");
            }
            int targetKey = Integer.parseInt(argument);
            User user = request.getAuthenticatedUser();
            int removedCount = collectionManager.removeGreaterKey(targetKey, user.getId());
            return new Response(true, "Успешно завершено. Удалено элементов: " + removedCount);

        } catch (NumberFormatException e) {
            return new Response(false, "Ошибка: Аргумент должен быть целым числом.");
        } catch (SQLException e) {
            return new Response(false, "Ошибка БД: " + e.getMessage());
        } catch (Exception e) {
            return new Response(false, "Внутренняя ошибка сервера при выполнении remove_greater_key: " + e.getMessage());
        }
    }

    @Override
    public String getDescription() {
        return "удалить из коллекции все элементы, ключ которых превышает заданный";
    }
}