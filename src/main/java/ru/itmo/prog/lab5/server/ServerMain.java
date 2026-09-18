package ru.itmo.prog.lab5.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.itmo.prog.lab5.server.managers.*;
import ru.itmo.prog.lab5.server.commands.*;
import ru.itmo.prog.lab5.server.network.UDPServer;
import java.io.*;
import java.sql.SQLException;


public class ServerMain {
    private static final Logger log = LoggerFactory.getLogger(ServerMain.class);

    public static void main(String[] args) {
        if (args.length < 3) {
            log.error("Ошибка: укажите логин и пароль для подключения к БД");
            return;
        }

        String bdHost = args[0];
        String bdLogin = args[1];
        String bdPassword = args[2];
        int port = 3054;
        log.info("Запуск сервера");


        DataBaseManager dataBaseManager = new DataBaseManager(bdHost, "studs", bdLogin, bdPassword);
        UserDAO userDAO = new UserDAO(dataBaseManager);
        DragonDAO dragonDAO = new DragonDAO(dataBaseManager);

        CollectionManager collectionManager;
        try {
            collectionManager = new CollectionManager(dragonDAO);
        } catch (SQLException e) {
            log.error("Не удалось загрузить коллекцию из БД: {}", e.getMessage());
            if (e.getCause() != null) {
                log.error("Причина: {}", e.getCause().getMessage());
            }
            return;
        }
        CommandManager commandManager = new CommandManager(userDAO);

        commandManager.register("help", new Help(commandManager));
        commandManager.register("info", new Info(collectionManager));
        commandManager.register("show", new Show(collectionManager));
        commandManager.register("clear", new Clear(collectionManager));
        commandManager.register("insert", new Insert(collectionManager));
        commandManager.register("update", new Update(collectionManager));
        commandManager.register("remove_lower", new RemoveLower(collectionManager));
        commandManager.register("filter_contains_name", new FilterContainsName(collectionManager));
        commandManager.register("print_field_ascending_color", new PrintFieldAscendingColor(collectionManager));
        commandManager.register("group_counting_by_killer", new GroupCountingByKiller(collectionManager));
        commandManager.register("remove_greater_key", new RemoveGreaterKey(collectionManager));
        commandManager.register("remove_all_by_color", new RemoveAllByColor(collectionManager));
        commandManager.register("remove_key", new RemoveKey(collectionManager));
        commandManager.register("remove_lower_key", new RemoveLowerKey(collectionManager));
        commandManager.register("register", new Register(userDAO));

        log.info("Сервер слушает порт: {}", port);

        try {
            UDPServer udpServer = new UDPServer(port, commandManager);
            udpServer.start();
        } catch (IOException e) {
            log.error("Ошибка запуска UDP-сервера: {}", e.getMessage());
        }

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("Завершение работы...");
            dataBaseManager.shutdown();
        }));
    }
}
