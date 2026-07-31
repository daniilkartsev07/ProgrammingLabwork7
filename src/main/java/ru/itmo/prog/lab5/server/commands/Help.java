package ru.itmo.prog.lab5.server.commands;
import ru.itmo.prog.lab5.server.managers.CommandManager;
import ru.itmo.prog.lab5.common.network.Request;
import ru.itmo.prog.lab5.common.network.Response;

/**
 * Команда 'help'. Выводит справку по доступным командам.
 */
public class Help implements Command {
    private final CommandManager commandManager;

    public Help(CommandManager commandManager) {
        this.commandManager = commandManager;
    }

    @Override
    public Response execute(Request request) {
        try {
            Thread.sleep(5000);
            String argument = request.getArgument();

            if (argument != null && !argument.isEmpty()) {
                return new Response(false, "Ошибка: команда 'help' не принимает аргументы.");
            }

            StringBuilder sb = new StringBuilder();
            sb.append("Справка по доступным командам\n");

            commandManager.getCommands().forEach((name, command) -> {
                sb.append(String.format(" %-25s : %s%n", name, command.getDescription()));
            });

            sb.append(String.format(" %-25s : %s%n", "exit", "завершить работу клиента"));

            return new Response(true, sb.toString());

        } catch (Exception e) {
            return new Response(false, "Ошибка сервера при генерации справки: " + e.getMessage());
        }
    }

    @Override
    public String getDescription() {
        return "вывести справку по доступным командам";
    }
}