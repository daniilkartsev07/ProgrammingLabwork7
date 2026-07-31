package ru.itmo.prog.lab5.server.commands;
import ru.itmo.prog.lab5.server.managers.CollectionManager;
import ru.itmo.prog.lab5.common.models.Dragon;
import ru.itmo.prog.lab5.common.network.Request;
import ru.itmo.prog.lab5.common.network.Response;
import java.util.HashMap;
import java.util.Map;

/**
 * Команда 'group_counting_by_killer'.
 * Группирует элементы коллекции по значению поля killer и выводит количество элементов в каждой группе.
 */
public class GroupCountingByKiller implements Command {
    private final CollectionManager collectionManager;

    public GroupCountingByKiller(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    /**
     * Выполняет группировку драконов по убийцам на стороне сервера.
     * @param request Объект запроса от клиента.
     * @return Объект ответа (Response) с результатами группировки.
     */
    @Override
    public Response execute(Request request) {
        try {
            String argument = request.getArgument();
            if (argument != null && !argument.isEmpty()) {
                return new Response(false, "Ошибка: команда 'group_counting_by_killer' не принимает аргументы.");
            }
            Map<Integer, Dragon> snapshot = collectionManager.getCollection();
            if (snapshot.isEmpty()) {
                return new Response(false, "Коллекция пуста, группы сформировать невозможно.");
            }
            Map<String, Long> groups = new HashMap<>();
            for (Dragon d : snapshot.values()) {
                String killerName = (d.getKiller() == null || d.getKiller().getName() == null) ? "killer отсутствует." : d.getKiller().getName();
                groups.put(killerName, groups.getOrDefault(killerName, 0L) + 1);
            }
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Распределение драконов по убийцам \n");
            groups.forEach((name, count) -> stringBuilder.append(String.format(" %s : %d шт.%n", name, count)));

            return new Response(true, stringBuilder.toString());
        } catch (Exception e) {
            return new Response(false, "Внутренняя ошибка сервера при группировке данных" + e.getMessage());
        }
    }


    @Override
    public String getDescription() {
        return "сгруппировать элементы коллекции по значению поля killer и вывести количество элементов в каждой группе";
    }
}