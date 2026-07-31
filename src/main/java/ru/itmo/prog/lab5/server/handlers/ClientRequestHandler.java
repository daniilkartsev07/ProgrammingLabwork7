package ru.itmo.prog.lab5.server.handlers;

import ru.itmo.prog.lab5.common.network.Request;
import ru.itmo.prog.lab5.common.network.Response;
import ru.itmo.prog.lab5.server.managers.CommandManager;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

/**
 * Обработчик клиентских запросов
 * Координирует чтение, обработку команд и отправку ответов.
 */
public class ClientRequestHandler {
    private final CommandManager commandManager;
    private final RequestDeserializer requestDeserializer;
    private final ResponseSerializer responseSerializer;

    public ClientRequestHandler(CommandManager commandManager, RequestDeserializer requestDeserializer, ResponseSerializer responseSerializer) {
        this.commandManager = commandManager;
        this.requestDeserializer = requestDeserializer;
        this.responseSerializer = responseSerializer;
    }
    /**
     * Обрабатывает входящий UDP-пакет от клиента.
     */

    public Response handle(DatagramChannel channel, ByteBuffer buffer, SocketAddress clientAddress) {
        try {
            byte[] data = extractData(buffer);
            Request request = requestDeserializer.deserialize(data);
            Response response = commandManager.execute(request);
            byte[] responseData = responseSerializer.serialize(response);
            ByteBuffer responseBuffer = ByteBuffer.wrap(responseData);
            channel.send(responseBuffer, clientAddress);

            return response;
        } catch (IOException e) {
            return sendError(channel, clientAddress, "Ошибка ввода-вывода при обработке запроса: " + e.getMessage());
        } catch (Exception e) {
            return sendError(channel, clientAddress, "Внутренняя ошибка сервера: " + e.getMessage());
        } finally {
            buffer.clear();
        }
    }

    /**
     * Извлекает байты из ByteBuffer.
     * @param buffer буфер с данными
     * @return массив байтов
     */

    private byte[] extractData(ByteBuffer buffer) {
        byte[] data = new byte[buffer.remaining()];
        buffer.get(data);
        return data;
    }

    /**
     * Отправляет сообщение об ошибке клиенту.
     * @param channel
     * @param clientAddress
     * @param message
     * @return
     */
    private Response sendError(DatagramChannel channel, SocketAddress clientAddress, String message) {
        try {
            Response errorResponse = new Response(false, message);
            byte[] errorData = responseSerializer.serialize(errorResponse);
            ByteBuffer errorBuffer = ByteBuffer.wrap(errorData);
            channel.send(errorBuffer, clientAddress);
            return errorResponse;
        } catch (IOException e) {
            System.err.println("не удалось отправить ошибку клиенту: " + e.getMessage());
            return new Response(false, "Критическая ошибка сервера");
        }
    }
}
