package ru.itmo.prog.lab5.server.commands;

import ru.itmo.prog.lab5.common.models.User;
import ru.itmo.prog.lab5.server.managers.CollectionManager;
import ru.itmo.prog.lab5.common.models.Dragon;
import ru.itmo.prog.lab5.common.network.Request;
import ru.itmo.prog.lab5.common.network.Response;

import java.sql.SQLException;
import java.util.Map;

/**
 * Команда 'update'. Обновляет элемент, ID которого равен заданному.
 */
public class Update implements Command {
    private final CollectionManager collectionManager;

    public Update(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    @Override
    public Response execute(Request request) {
        try {
            String argument = request.getArgument();
            if (argument == null || argument.isEmpty()) {
                return new Response(false, "Ошибка: введите ID после команды 'update'.");
            }

            Integer id = Integer.parseInt(argument);

            Dragon updatedDragon = request.getDragon();
            if (updatedDragon == null) {
                return new Response(false, "Ошибка: сервер не получил обновленные данные дракона.");
            }

            if (!updatedDragon.validate()) {
                return new Response(false, "Ошибка: данные дракона не прошли валидацию.");
            }
            updatedDragon.setId(id);

            User user = request.getAuthenticatedUser();
            boolean success = collectionManager.update(updatedDragon, user.getId());
            if (!success) {
                return new Response(false, "Ошибка: дракон с ID " + id + "не найден.");
            }
            return new Response(true, "Данные дракона с ID " + id + " успешно обновлены.");

        } catch (NumberFormatException e) {
            return new Response(false, "Ошибка: ID должен быть целым числом.");
        } catch (SQLException e) {
            return new Response(false, "Ошибка БД: " + e.getMessage());
        } catch (Exception e) {
            return new Response(false, "Внутренняя ошибка сервера: " + e.getMessage());
        }
    }

    @Override
    public String getDescription() {
        return "обновить значение элемента коллекции, ID которого равен заданному";
    }
}
