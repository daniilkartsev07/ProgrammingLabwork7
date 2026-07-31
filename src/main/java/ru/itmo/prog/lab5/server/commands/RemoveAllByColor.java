package ru.itmo.prog.lab5.server.commands;

import ru.itmo.prog.lab5.common.models.User;
import ru.itmo.prog.lab5.server.managers.CollectionManager;
import ru.itmo.prog.lab5.common.models.Color;
import ru.itmo.prog.lab5.common.network.Request;
import ru.itmo.prog.lab5.common.network.Response;

import java.sql.SQLException;

/**
 * Команда 'remove_all_by_color'.
 * Удаляет из коллекции все элементы, значение поля color которого эквивалентно заданному.
 */
public class RemoveAllByColor implements Command {
    private final CollectionManager collectionManager;

    public RemoveAllByColor(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    /**
     * Выполняет удаление элементов по цвету на стороне сервера.
     * @param request Объект запроса от клиента, содержащий строку с цветом.
     * @return Объект ответа (Response).
     */
    @Override
    public Response execute(Request request) {
        try {
            String argument = request.getArgument();


            if (argument == null || argument.isEmpty()) {
                return new Response(false, "Ошибка: введите цвет в качестве аргумента (например, remove_all_by_color red).");
            }
            Color color;
            try {
                color = Color.valueOf(argument.toUpperCase().trim());
            } catch (IllegalArgumentException e) {
                return new Response(false, "Ошибка: указанного цвета нет в списке доступных цветов.");
            }
            User user = request.getAuthenticatedUser();
            int removedCount = collectionManager.removeAllByColor(color, user.getId());

            return new Response(true, "Успешно удалено элементов с цветом " + color + ": " + removedCount);

        } catch (SQLException e) {
            return new Response(false, "Ошибка БД: " + e.getMessage());
        } catch (Exception e) {
            return new Response(false, "Внутренняя ошибка сервера при удалении по цвету: " + e.getMessage());
        }
    }

    @Override
    public String getDescription() {
        return "удалить из коллекции все элементы, значение поля color которого эквивалентно заданному";
    }
}