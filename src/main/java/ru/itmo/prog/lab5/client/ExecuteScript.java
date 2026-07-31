package ru.itmo.prog.lab5.client;

import ru.itmo.prog.lab5.client.network.ClientManager;
import ru.itmo.prog.lab5.common.models.Dragon;
import ru.itmo.prog.lab5.common.network.Request;
import ru.itmo.prog.lab5.common.network.Response;
import ru.itmo.prog.lab5.common.utilites.Asker;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

/**
 * Класс для обработки скриптов на стороне Клиента.
 */
public class ExecuteScript {

    private static final Set<String> activeScripts = new HashSet<>();
    private static final Set<String> DRAGONS_COMMANDS = Set.of("insert", "update", "remove_lower");
    private final ClientManager clientManager;
    private final String login;
    private final String password;

    public ExecuteScript(ClientManager clientManager, String login, String password) {
        this.clientManager = clientManager;
        this.login = login;
        this.password = password;
    }

    /**
     * Читает скрипт, формирует запросы и отправляет их на сервер.
     * @param argument Путь к файлу скрипта
     */
    public void execute(String argument) {
        if (argument.isEmpty()) {
            System.out.println("Ошибка скрипта: Введите имя файла скрипта.");
            return;
        }

        File file = new File(argument);
        String absolutePath = file.getAbsolutePath();

        if (activeScripts.contains(absolutePath)) {
            System.out.println("Ошибка скрипта: Обнаружена рекурсия! Скрипт " + file.getName() + " уже выполняется.");
            return;
        }

        activeScripts.add(absolutePath);

        try (Scanner scriptScanner = new Scanner(file)) {
            Asker scriptAsker = new Asker(scriptScanner, true);

            System.out.println("Начало выполнения скрипта: " + file.getName());

            while (scriptScanner.hasNextLine()) {
                String line = scriptScanner.nextLine().trim();
                if (line.isEmpty()) continue;

                String[] tokens = (line + " ").split("\\s+", 2);
                String commandName = tokens[0].toLowerCase().trim();
                String commandArg = tokens[1].trim();


                if (commandName.equals("exit")) {
                    System.out.println("Скрипт вызвал команду exit. Прерывание.");
                    break;
                }


                if (commandName.equals("execute_script")) {
                    execute(commandArg);
                    continue;
                }

                Request request;
                if (DRAGONS_COMMANDS.contains(commandName)) {
                    try {
                        Dragon dragon = scriptAsker.createDragon();
                        request = new Request(commandName, commandArg, dragon, login, password);
                    } catch (Exception e) {
                        System.out.println("Ошибка скрипта: Не удалось считать данные дракона из файла: " + e.getMessage());
                        break;
                    }
                } else {
                    request = new Request(commandName, commandArg, login, password);
                }


                System.out.println("\nИсполнение команды из скрипта: [" + commandName + "]");
                Response response = clientManager.sendRequest(request);

                if (response != null) {
                    if (response.isSuccess()) {
                        System.out.println(response.getMessage());
                    } else {
                        System.out.println("Ошибка скрипта: " + response.getMessage());
                    }
                }
            }

            System.out.println("Выполнение скрипта " + file.getName() + " успешно завершено.");

        } catch (FileNotFoundException e) {
            System.out.println("Ошибка скрипта: Файл скрипта не найден: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Ошибка скрипта: " + e.getMessage());
        } finally {
            activeScripts.remove(absolutePath);
        }
    }
}
