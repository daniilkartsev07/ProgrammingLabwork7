package ru.itmo.prog.lab5.server.commands;

import ru.itmo.prog.lab5.common.models.User;
import ru.itmo.prog.lab5.server.managers.CollectionManager;
import ru.itmo.prog.lab5.common.network.Request;
import ru.itmo.prog.lab5.common.network.Response;

import java.sql.SQLException;

/**
 * Команда 'remove_lower_key'. Удаляет элементы с ключом меньше заданного.
 */
public class RemoveLowerKey implements Command {
    private final CollectionManager collectionManager;

    public RemoveLowerKey(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    /**
     * Выполняет удаление элементов с ключом меньше заданного на стороне сервера.
     * @param request Объект запроса от клиента.
     * @return Объект ответа с результатом выполнения.
     */
    @Override
    public Response execute(Request request) {
        try {
            String argument = request.getArgument();


            if (argument == null || argument.isEmpty()) {
                return new Response(false, "Ошибка: Команда 'remove_lower_key' требует числовой ключ в качестве аргумента.");
            }


            int targetKey = Integer.parseInt(argument);
            User user = request.getAuthenticatedUser();
            int removedCount = collectionManager.removeLowerKey(targetKey, user.getId());

            return new Response(true, "Успешно завершено. Удалено элементов: " + removedCount);

        } catch (NumberFormatException e) {
            return new Response(false, "Ошибка: Аргумент должен быть целым числом.");
        } catch (SQLException e) {
            return new Response(false, "Ошибка БД: " + e.getMessage());
        } catch (Exception e) {
            return new Response(false, "Внутренняя ошибка сервера: " + e.getMessage());
        }
    }

    @Override
    public String getDescription() {
        return "удалить из коллекции все элементы, ключ которых меньше, чем заданный";
    }
}

