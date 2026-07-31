package ru.itmo.prog.lab5.client.network;

import ru.itmo.prog.lab5.common.network.Request;
import ru.itmo.prog.lab5.common.network.Response;

import java.io.*;
import java.net.*;

/**
 * Реализация клиента с ретраем и логированием состояния.
 * Использует DatagramSocket для надёжной работы таймаутов.
 */

public class RetryClientManager implements ClientManager {
    private final String host;
    private final int port;
    private final int timeOut;
    private final int maxRetries;
    private static final int BUFFER_SIZE = 8192;

    public RetryClientManager(String host, int port, int timeOut, int maxRetries) {
        this.host = host;
        this.port = port;
        this.timeOut = timeOut;
        this.maxRetries = maxRetries;
    }

    @Override
    public Response sendRequest(Request request) {
        byte[] requestData;
        try {
            requestData = serializeObject(request);
        } catch (IOException e) {
            return new Response(false, "Ошибка сериализации запроса: " + e.getMessage());
        }
        try (DatagramSocket socket = new DatagramSocket()) {
            for (int attempt = 1; attempt <= maxRetries; attempt++) {
                int currentTimeout = timeOut * attempt;
                System.out.println("Попытка " + attempt + " из " + maxRetries + ": отправка запроса... (тайм аут " + currentTimeout + "мс)");
                long start = System.currentTimeMillis();

                try {
                    socket.setSoTimeout(currentTimeout);

                    byte[] sendBuffer = requestData;
                    DatagramPacket sendPacket = new DatagramPacket(sendBuffer, sendBuffer.length,
                            InetAddress.getByName(host), port);
                    socket.send(sendPacket);

                    byte[] receiveBuffer = new byte[BUFFER_SIZE];
                    DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);

                    System.out.println(" Ожидание ответа (таймаут: " + currentTimeout + " мс)...");
                    socket.receive(receivePacket);

                    long elapsed = System.currentTimeMillis() - start;
                    System.out.println(" Ответ получен за " + elapsed + " мс");

                    byte[] responseData = receivePacket.getData();
                    int length = receivePacket.getLength();
                    byte[] trimmed = new byte[length];
                    System.arraycopy(responseData, 0, trimmed, 0, length);

                    return (Response) deserializeObject(trimmed);

                } catch (SocketTimeoutException e) {
                    long elapsed = System.currentTimeMillis() - start;
                    System.out.println("Таймаут! Сервер не ответил за " + elapsed + " мс");
                } catch (UnknownHostException e) {
                    System.out.println("Неверный адрес сервера: " + host);
                    return new Response(false, "Неверный хост: " + host);
                } catch (IOException | ClassNotFoundException e) {
                    System.out.println("Попытка " + attempt + " не удалась: " +
                            e.getClass().getSimpleName() + " - " + e.getMessage());
                }

                if (attempt < maxRetries) {
                    int delay = 500 * attempt;
                    System.out.println("Пауза " + delay + " мс перед повтором...");
                    try {
                        Thread.sleep(delay);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        return new Response(false, "Операция прервана");
                    }
                }
            }
        } catch (SocketException e) {
            return new Response(false, "Не удалось создать сокет: " + e.getMessage());
        }
        return new Response(false, "Сервер не отвечает после " + maxRetries + " попыток");
    }

    private byte[] serializeObject(Object obj) throws IOException {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(bos)) {
            oos.writeObject(obj);
            oos.flush();
            return bos.toByteArray();
        }
    }

    private Object deserializeObject(byte[] data) throws IOException, ClassNotFoundException {
        try (ByteArrayInputStream bis = new ByteArrayInputStream(data);
             ObjectInputStream ois = new ObjectInputStream(bis)) {
            return ois.readObject();
        }
    }
}