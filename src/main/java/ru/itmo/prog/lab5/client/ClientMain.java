package ru.itmo.prog.lab5.client;

import ru.itmo.prog.lab5.client.network.ClientManager;
import ru.itmo.prog.lab5.client.network.RetryClientManager;
import ru.itmo.prog.lab5.common.models.Dragon;
import ru.itmo.prog.lab5.common.network.Request;
import ru.itmo.prog.lab5.common.network.Response;
import ru.itmo.prog.lab5.common.utilites.Asker;

import java.util.Scanner;
import java.util.Set;


public class ClientMain {
    private static final Set<String> DRAGON_COMMANDS = java.util.Set.of("insert", "update", "remove_lower");

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Asker asker = new Asker(scanner);

        ClientManager clientManager = new RetryClientManager("localhost", 3054, 2000, 3);

        System.out.println("Интерактивный клиент запущен");

        String[] credentials = authorise(scanner, clientManager);
        String login = credentials[0];
        String password = credentials[1];

        System.out.println("Введите 'help' для списка доступных команд или 'exit' для выхода.");

        while (true) {
            System.out.print("> ");
            if (!scanner.hasNextLine()) break;

            String input = scanner.nextLine().trim();
            if (input.isEmpty()) continue;


            String[] parts = input.split("\\s+", 2);
            String commandName = parts[0].toLowerCase();
            String argument = parts.length > 1 ? parts[1] : "";

            if (commandName.equals("exit")) {
                if (!argument.isEmpty()) {
                    System.out.println("Ошибка: Команда 'exit' не принимает аргументы.");
                    continue;
                }
                System.out.println("Завершение работы клиента. До свидания!");
                break;
            }
            if (commandName.equals("execute_script")) {
                ExecuteScript scriptHandler = new ExecuteScript(clientManager, login, password);
                scriptHandler.execute(argument);
                continue;
            }


            Request request;
            if (DRAGON_COMMANDS.contains(commandName)) {
                System.out.println("Введите характеристики Дракона:");
                Dragon dragon = asker.createDragon();
                request = new Request(commandName, argument, dragon, login, password);
            } else {
                request = new Request(commandName, argument, login, password);
            }
            Response response = clientManager.sendRequest(request);
            printResponse(response);
        }
        scanner.close();
    }

    /**
     * Спрашивает логин/действие (войти/зарегистрироваться), повторяет попытку
     * пока сервер не подтвердит успех.
     * @param scanner
     * @param clientManager
     * @return
     */
    private static String[] authorise(Scanner scanner, ClientManager clientManager) {
        while (true) {
            System.out.println("Выберите действие: 1 - войти, 2 - зарегистрироваться.");
            System.out.println("> ");
            String choice = scanner.hasNextLine() ? scanner.nextLine().trim() : "1";

            System.out.println("Логин: ");
            String login = scanner.nextLine().trim();
            System.out.println("Пароль: ");
            String password = scanner.nextLine().trim();

            if ("2".equals(choice)) {
                Request registerRequest = new Request("register", "", login, password);
                Response response = clientManager.sendRequest(registerRequest);
                printResponse(response);
                if (response != null && response.isSuccess()) {
                    return new String[]{login, password};
                }
                continue;
            }
            Request probe = new Request("help", "", login, password);
            Response response = clientManager.sendRequest(probe);
            if (response != null && response.isSuccess()) {
                return new String[]{login, password};
            }
            System.out.println("Не удалось войти: " + (response != null ? response.getMessage() : "нет ответа от сервера"));
    }
    }

    private static void printResponse(Response response) {
        if (response == null) return;
        if (response.isSuccess()) {
            System.out.println(response.getMessage());
        } else {
            System.out.println("Ошибка: " + response.getMessage());
        }
    }
}

