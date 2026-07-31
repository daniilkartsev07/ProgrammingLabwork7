package ru.itmo.prog.lab5.server.commands;

import ru.itmo.prog.lab5.common.models.User;
import ru.itmo.prog.lab5.server.managers.CollectionManager;
import ru.itmo.prog.lab5.common.models.Dragon;
import ru.itmo.prog.lab5.common.network.Request;
import ru.itmo.prog.lab5.common.network.Response;

import java.sql.SQLException;

/**
 * Команда 'remove_lower'. Удаляет из коллекции все элементы, меньшие, чем заданный.
 */
public class RemoveLower implements Command {
    private final CollectionManager collectionManager;

    public RemoveLower(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    /**
     * Выполняет команду удаления меньших элементов на стороне сервера.
     * @param request Объект запроса от клиента, содержащий эталонного дракона для сравнения.
     * @return Объект ответа (Response) со статусом выполнения.
     */
    @Override
    public Response execute(Request request) {
        Dragon dragon = request.getDragon();
        if (dragon == null) {
            return new Response(false, "Ошибка: сервер не получил объект для сравнения.");
        }
        if (!dragon.validate()) {
            return new Response(false, "Ошибка: дракон не прошел валидацию.");
        }
        User user = request.getAuthenticatedUser();
        try {
            int removed = collectionManager.removeLower(dragon, user.getId());
            return new Response(true, "Удалено элементов: " + removed);
        } catch (SQLException e) {
            return new Response(false, "Ошибка БД: " + e.getMessage());
        }
    }

    @Override
    public String getDescription() {
        return "удалить из коллекции все элементы, меньшие, чем заданный";
    }
}
