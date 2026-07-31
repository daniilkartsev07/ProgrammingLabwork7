package ru.itmo.prog.lab5.server.commands;
import ru.itmo.prog.lab5.common.models.User;
import ru.itmo.prog.lab5.server.managers.CollectionManager;
import ru.itmo.prog.lab5.common.network.Request;
import ru.itmo.prog.lab5.common.network.Response;
import java.sql.SQLException;

/**
 * Команда 'clear'. Очищает коллекцию на сервере.
 */
public class Clear implements Command {
    private final CollectionManager collectionManager;

    public Clear(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    @Override
    public Response execute(Request request) {

        if (request.getArgument() != null && !request.getArgument().isEmpty()) {
            return new Response(false, "Ошибка: команда 'clear' не принимает аргументы.");
        }

        User user = request.getAuthenticatedUser();
        try {
            int deleted = collectionManager.clear(user.getId());
            return new Response(true, "Коллекция успешно очищена. Удалено: " + deleted + "драконов.");

        } catch (SQLException e) {
            return new Response(false, "Ошибка БД: " + e.getMessage());
        }
    }

    @Override
    public String getDescription() {
        return "очистить свою коллекцию";
    }
}
