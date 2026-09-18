package ru.itmo.prog.lab5.server.managers;
import ru.itmo.prog.lab5.common.models.*;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;


/**
 * Класс для управления коллекцией.
 */


public class CollectionManager {
    private final Map<Integer, Dragon> dragonMap;
    private final DragonDAO dragonDAO;
    private final java.time.LocalDateTime initializationDate;

    public CollectionManager(DragonDAO dragonDAO) throws SQLException {
        this.dragonDAO = dragonDAO;
        this.initializationDate = java.time.LocalDateTime.now();
        this.dragonMap = new HashMap<>(dragonDAO.loadAll());
    }

    /**
     * Отдает копию коллекции для команд чтения.
     * защищает dragonMap от изменений в обход БД и от гонок,
     * пока другой поток параллельно пишет в мапу.
     */
    public synchronized Map<Integer, Dragon> getCollection() {
        return new HashMap<>(dragonMap);
    }

    public synchronized boolean insert(Dragon dragon, long ownerId) throws SQLException {
        Dragon saved = dragonDAO.insert(dragon, ownerId);
        if (saved == null) {
            return false;
        }
        dragonMap.put(saved.getId(), saved);
        return true;
    }

    public synchronized boolean update(Dragon dragon, long ownerId) throws SQLException {
        if (!dragonDAO.update(dragon, ownerId)) return false;
        dragonMap.put(dragon.getId(), dragon);
        return true;
    }

    public synchronized int clear(long ownerId) throws SQLException {
        int deleted = dragonDAO.deleteAllByOwner(ownerId);
        dragonMap.entrySet().removeIf(e -> ownerId == e.getValue().getOwnerId());
        return deleted;
    }

    public synchronized boolean removeKey(Integer id, long ownerId) throws SQLException {
        if (!dragonDAO.delete(id, ownerId)) return false;
        dragonMap.remove(id);
        return true;
    }

    public String printFieldAscendingColor() {
        return getCollection().values().stream().map(Dragon::getColor).sorted().map(String::valueOf).collect(Collectors.joining("\n"));
    }

    /**
     * Удаляет элементы, которые меньше переданного из compareTo
     * Читает из памяти, удаление проводит из DAO,
     * чтобы не потерять синхронизацию с БД
     *
     * @param dragon
     * @param ownerId
     * @return
     * @throws SQLException
     */
    public synchronized int removeLower(Dragon dragon, long ownerId) throws SQLException {
        List<Integer> toRemove = dragonMap.values().stream()
                .filter(d -> ownerId == d.getOwnerId())
                .filter(d -> d.compareTo(dragon) < 0)
                .map(Dragon::getId)
                .collect(Collectors.toList());

        int removed = 0;
        for (Integer id : toRemove) {
            if (dragonDAO.delete(id, ownerId)) {
                dragonMap.remove(id);
                removed++;
            }
        }
        return removed;
    }
    public synchronized int removeAllByColor(Color color, long ownerId) throws SQLException {
        List<Integer> toRemove = dragonMap.values().stream()
                .filter(d -> ownerId == d.getOwnerId())
                .filter(d -> d.getColor() == color)
                .map(Dragon::getId)
                .collect(Collectors.toList());
        int removed = 0;
        for (Integer id : toRemove) {
            if (dragonDAO.delete(id, ownerId)) {
                dragonMap.remove(id);
                removed++;
            }
        }
        return removed;
}

    @Override
    public String toString() {
        if (dragonMap.isEmpty()) return "Коллекция пуста.";
        return dragonMap.values().stream().map(Dragon::toString).collect(Collectors.joining("\n"));

    }
    public String getInfo() {
        Map<Integer, Dragon> snapshot = getCollection();
        return "Тип: HashMap\n" + "Дата инициализации: " + initializationDate + "\n" +
                "Количество элементов: " + snapshot.size();
    }

    public java.time.LocalDateTime getInitializationDate() {
        return initializationDate;
    }
    public synchronized int removeGreaterKey(int targetId, long ownerId) throws SQLException {
        List<Integer> toRemove = dragonMap.values().stream()
                .filter(d -> ownerId == d.getOwnerId())
                .filter(d -> d.getId() > targetId)
                .map(Dragon::getId)
                .collect(Collectors.toList());
        int removed = 0;
        for (Integer id : toRemove) {
            if (dragonDAO.delete(id, ownerId)) {
                dragonMap.remove(id);
                removed++;
            }
        }
        return removed;
    }

    public synchronized int removeLowerKey(int targetId, long ownerId) throws SQLException {
        List<Integer> toRemove = dragonMap.values().stream()
                .filter(d -> ownerId == d.getOwnerId())
                .filter(d -> d.getId() < targetId)
                .map(Dragon::getId)
                .collect(Collectors.toList());
        int removed = 0;
        for (Integer id : toRemove) {
            if (dragonDAO.delete(id, ownerId)) {
                dragonMap.remove(id);
                removed++;
            }
        }
        return removed;
    }
}
