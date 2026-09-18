package ru.itmo.prog.lab5.server.managers;

import ru.itmo.prog.lab5.common.models.*;
import java.sql.*;
import java.time.LocalDate;
import java.util.*;

/**
 * Data Access Object для работы с таблицей драконов.
 * Отвечает за загрузку коллекции, добавление, обновление и удаление драконов.
 */

public class DragonDAO {
    private final DataBaseManager dataBaseManager;

    public DragonDAO(DataBaseManager dataBaseManager) {
        this.dataBaseManager = dataBaseManager;
    }

    /**
     * Загружает всю коллекцию из БД при старте сервера.
     */
    public Map<Integer, Dragon> loadAll() throws SQLException {
        Map<Integer, Dragon> map = new HashMap<>();
        String sql = "SELECT * FROM dragons";
        try (Connection connection = dataBaseManager.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                Dragon dragon = mapRow(resultSet);
                map.put(dragon.getId(), dragon);
            }
        }
        return map;
    }

    /**
     * Вставляет дракона, получает id из sequence (RETURNING id).
     */
    public Dragon insert(Dragon dragon, long ownerId) throws SQLException {
        String sql = "INSERT INTO dragons(name, coord_x, coord_y, creation_date, age, color, type, " +
                "character, killer_name, killer_height, killer_location_x, killer_location_y, " +
                "killer_location_z, killer_location_name, owner_id) " +
                "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) RETURNING id";
        try (Connection connection = dataBaseManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            fillInsertParams(preparedStatement, dragon, ownerId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    dragon.setId(resultSet.getInt("id"));
                    dragon.setOwnerId(ownerId);
                    return dragon;
                }
            }
        }
        return null;
    }

    /**
     * Обновляет данные дракона.
     * @param dragon
     * @param ownerId
     * @return
     * @throws SQLException
     */
    public boolean update(Dragon dragon, long ownerId) throws SQLException {
        String sql = "UPDATE dragons SET name=?, coord_x=?, coord_y=?, age=?, color=?, type=?, " +
                "character=?, killer_name=?, killer_height=?, killer_location_x=?, killer_location_y=?, " +
                "killer_location_z=?, killer_location_name=? WHERE id=? AND owner_id=?";
        try (Connection connection = dataBaseManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            int i = 1;
            preparedStatement.setString(i++, dragon.getName());
            preparedStatement.setDouble(i++, dragon.getCoordinates().getX());
            preparedStatement.setFloat(i++, dragon.getCoordinates().getY());
            preparedStatement.setObject(i++, dragon.getAge());
            preparedStatement.setString(i++, dragon.getColor() == null ? null : dragon.getColor().name());
            preparedStatement.setString(i++, dragon.getType() == null ? null : dragon.getType().name());
            preparedStatement.setString(i++, dragon.getCharacter() == null ? null : dragon.getCharacter().name());
            Person killer = dragon.getKiller();
            Location killerLocation = killer == null ? null : killer.getLocation();
            preparedStatement.setString(i++, killer == null ? null : killer.getName());
            preparedStatement.setObject(i++, killer == null ? null : killer.getHeight());
            preparedStatement.setObject(i++, killerLocation == null ? null : killerLocation.getX());
            preparedStatement.setObject(i++, killerLocation == null ? null : killerLocation.getY());
            preparedStatement.setObject(i++, killerLocation == null ? null : killerLocation.getZ());
            preparedStatement.setString(i++, killerLocation == null ? null : killerLocation.getName());
            preparedStatement.setInt(i++, dragon.getId());
            preparedStatement.setLong(i, ownerId);
            return preparedStatement.executeUpdate() > 0;
        }
    }

    /**
     * Удаляет дракона по ID
     * @param id
     * @param ownerId
     * @return
     * @throws SQLException
     */
    public boolean delete(int id, long ownerId) throws SQLException {
        String sql = "DELETE FROM dragons WHERE id=? AND owner_id=?";
        try (Connection connection = dataBaseManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, id);
            preparedStatement.setLong(2, ownerId);
            return preparedStatement.executeUpdate() > 0;
        }
    }

    /**
     * Удаляет всех драконов, принадлежазщих пользователю.
     * @param ownerId
     * @return
     * @throws SQLException
     */
    public int deleteAllByOwner(long ownerId) throws SQLException {
        String sql = "DELETE FROM dragons WHERE owner_id=?";
        try (Connection connection = dataBaseManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setLong(1, ownerId);
            return preparedStatement.executeUpdate();
        }
    }

    /**
     * для insert(): без id (его даёт sequence), но с creation_date.
     */
    private void fillInsertParams(PreparedStatement preparedStatement, Dragon dragon, long ownerId) throws SQLException {
        int i = 1;
        preparedStatement.setString(i++, dragon.getName());
        preparedStatement.setDouble(i++, dragon.getCoordinates().getX());
        preparedStatement.setFloat(i++, dragon.getCoordinates().getY());
        preparedStatement.setTimestamp(i++, Timestamp.valueOf(dragon.getCreationDate().atStartOfDay()));
        preparedStatement.setObject(i++, dragon.getAge());
        preparedStatement.setString(i++, dragon.getColor() == null ? null : dragon.getColor().name());
        preparedStatement.setString(i++, dragon.getType() == null ? null : dragon.getType().name());
        preparedStatement.setString(i++, dragon.getCharacter() == null ? null : dragon.getCharacter().name());
        Person killer = dragon.getKiller();
        Location killerLocation = killer == null ? null : killer.getLocation();
        preparedStatement.setString(i++, killer == null ? null : killer.getName());
        preparedStatement.setObject(i++, killer == null ? null : killer.getHeight());
        preparedStatement.setObject(i++, killerLocation == null ? null : killerLocation.getX());
        preparedStatement.setObject(i++, killerLocation == null ? null : killerLocation.getY());
        preparedStatement.setObject(i++, killerLocation == null ? null : killerLocation.getZ());
        preparedStatement.setString(i++, killerLocation == null ? null : killerLocation.getName());
        preparedStatement.setLong(i, ownerId);
    }

    /**
     *  Собирает Dragon из строки результата SELECT.
     */

    private Dragon mapRow(ResultSet resultSet) throws SQLException {
        Integer id = resultSet.getInt("id");
        String name = resultSet.getString("name");
        Coordinates coordinates = new Coordinates(
                resultSet.getLong("coord_x"),
                resultSet.getInt("coord_y")
        );
        LocalDate creationDate = resultSet.getTimestamp("creation_date").toLocalDateTime().toLocalDate();
        long ageRaw = resultSet.getLong("age");
        Long age = resultSet.wasNull() ? null : ageRaw;
        Color color = resultSet.getString("color") == null ? null : Color.valueOf(resultSet.getString("color"));
        DragonType type = resultSet.getString("type") == null ? null : DragonType.valueOf(resultSet.getString("type"));
        DragonCharacter character = resultSet.getString("character") == null ? null : DragonCharacter.valueOf(resultSet.getString("character"));

        Person killer = null;
        String killerName = resultSet.getString("killer_name");
        if (killerName != null) {
            Double height = (Double) resultSet.getObject("killer_height");
            Integer locX = (Integer) resultSet.getObject("killer_location_x");
            int locY = resultSet.getInt("killer_location_y");
            Double locZ = (Double) resultSet.getObject("killer_location_z");
            String locName = resultSet.getString("killer_location_name");
            Location location = (locX == null) ? null : new Location(locX, locY, locZ, locName);
            killer = new Person(killerName, height, location);
        }

        Dragon dragon = new Dragon(id, name, coordinates, creationDate, age, color, type, character, killer);
        dragon.setOwnerId(resultSet.getLong("owner_id"));
        return dragon;
    }
}