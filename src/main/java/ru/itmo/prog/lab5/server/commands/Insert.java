package ru.itmo.prog.lab5.server.commands;

import ru.itmo.prog.lab5.common.models.User;
import ru.itmo.prog.lab5.server.managers.CollectionManager;
import ru.itmo.prog.lab5.common.models.Dragon;
import ru.itmo.prog.lab5.common.network.Request;
import ru.itmo.prog.lab5.common.network.Response;

import java.sql.SQLException;

/**
 * Команда 'insert'. Добавляет новый элемент в коллекцию по заданному ключу.
 */
public class Insert implements Command {
    private final CollectionManager collectionManager;

    public Insert(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    /**
     * Выполняет команду добавления на стороне сервера.
     * @param request Объект запроса, содержащий ключ и созданного на клиенте дракона.
     * @return Объект ответа (Response) со статусом выполнения.
     */
    @Override
    public Response execute(Request request) {
        try {
            Dragon newDragon = request.getDragon();
            if (newDragon == null) {
                return new Response(false, "Ошибка: Сервер не получил данные дракона.");
            }
            if (!newDragon.validate()) {
                return new Response(false, "Ошибка: Данные дракона не прошли валидацию на сервере.");
            }
            User user = request.getAuthenticatedUser();
            try {

                boolean success = collectionManager.insert(newDragon, user.getId());
                if (!success) {
                    return new Response(false, "Ошибка: не удалось сохранить дракона в БД.");
                }
                return new Response(true, "Дракон успешно добавлен, id=" + newDragon.getId());
            } catch (SQLException e) {
                return new Response(false, "Ошибка БД: " + e.getMessage());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @return Описание команды для вывода в help
     */
    @Override
    public String getDescription() {
        return "добавить новый элемент с заданным ключом (insert key {element})";
    }
}
