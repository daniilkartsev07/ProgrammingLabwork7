package ru.itmo.prog.lab5.server.commands;

import ru.itmo.prog.lab5.common.models.User;
import ru.itmo.prog.lab5.server.managers.CollectionManager;
import ru.itmo.prog.lab5.common.network.Request;
import ru.itmo.prog.lab5.common.network.Response;

import java.sql.SQLException;

/**
 * Команда 'remove_key'. Удаляет элемент из коллекции по его ключу.
 */
public class RemoveKey implements Command {
    private final CollectionManager collectionManager;

    public RemoveKey(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    @Override
    public Response execute(Request request) {
        try {
            String argument = request.getArgument();
            if (argument == null || argument.isEmpty()) {
                return new Response(false, "Ошибка: введите ID после комнады 'remove_key'.");
            }

            Integer id = Integer.parseInt(argument);

            User user = request.getAuthenticatedUser();
            boolean success = collectionManager.removeKey(id, user.getId());
            if (!success) {
                return new Response(false, "Ошибка: дракон с ID " + id + " не найден.");
            }
            return new Response(true, "Дракон с ID " + id + "успешно удалён.");
        } catch (NumberFormatException e) {
            return new Response(false, "Ошибка: Ключ должен быть целым числом.");
        } catch (SQLException e) {
            return new Response(false, "Ошибка БД: " + e.getMessage());
        }
    }
    @Override
    public String getDescription() {
        return "удалить элемент из коллекции по его ключу";
    }
}
