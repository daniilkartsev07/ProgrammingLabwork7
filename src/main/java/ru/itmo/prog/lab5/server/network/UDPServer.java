package ru.itmo.prog.lab5.server.network;

import ru.itmo.prog.lab5.common.network.Request;
import ru.itmo.prog.lab5.common.network.Response;
import ru.itmo.prog.lab5.server.handlers.RequestDeserializer;
import ru.itmo.prog.lab5.server.handlers.ResponseSerializer;
import ru.itmo.prog.lab5.server.managers.CommandManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

/**
 * UPD-сервер с многопоточной обработкой запросов.
 * Cached pool читает входящие дотаграммы, Fixed pool выполняет команды
 * Cached pool отправляет ответы клиентам.
 */
public class UDPServer {
    private static final Logger logger = LoggerFactory.getLogger(UDPServer.class);
    private final DatagramSocket datagramSocket;
    private final CommandManager commandManager;
    private final RequestDeserializer requestDeserializer;
    private final ResponseSerializer responseSerializer;
    private final int port;
    private static final int BUFFER_SIZE = 8192;
    private static final int MAX_CURRENT_READS = 10;

    private final ExecutorService readPool = Executors.newCachedThreadPool();
    private final ExecutorService processPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    private final ExecutorService sendPool = Executors.newCachedThreadPool();

    private final Semaphore readSlots = new Semaphore(MAX_CURRENT_READS);
    private volatile boolean running = true;


    public UDPServer(int port, CommandManager commandManager) throws IOException {
        this.datagramSocket = new DatagramSocket(port);
        this.port = port;
        this.commandManager = commandManager;
        this.requestDeserializer = new RequestDeserializer();
        this.responseSerializer = new ResponseSerializer();
        logger.info("UDP-сервер инициализирован на порту: {}", port);
    }

        /**
         * Цикл обработки, работает до прерывания.
         */

        public void start() {
            logger.info("UDP-сервер запущен и ожидает подключения...");
            logger.info("Нажмите ctrl+c для его остановки");
            Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown));
            while (running) {
                try {
                    readSlots.acquire();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
                byte[] buffer = new byte[BUFFER_SIZE];
                DatagramPacket datagramPacket = new DatagramPacket(buffer, buffer.length);

                readPool.submit(() -> {
                    try {
                        datagramSocket.receive(datagramPacket);
                        logger.debug("Получен запрос от {} ({} байт", datagramPacket.getSocketAddress(), datagramPacket.getLength());
                        processPool.submit(() -> handleRequest(datagramPacket));
                    } catch (IOException e) {
                        if (running) {
                            logger.error("Ошибка обработки запроса: {}", e.getMessage());
                        }
                    } finally {
                        readSlots.release();
                    }
                });
            }
        }
            /**
         * Обрабатывает полученный запрос: десериализирует, выполняет команду,
             * передает готовый ответ на отправку. Выполняется в потоке Fixed pool.
         */

     private void handleRequest(DatagramPacket datagramPacket) {
         Response response;
         try {
             byte[] data = new byte[datagramPacket.getLength()];
             System.arraycopy(datagramPacket.getData(), 0, data, 0, datagramPacket.getLength());
             Request request = requestDeserializer.deserialize(data);
             response = commandManager.execute(request);
             logger.info("Запрос обработан");
        } catch (Exception e) {
             logger.error("Ошибка обработки запроса: {}", e.getMessage());
             response = new Response(false, "Внутренняя ошибка сервера: " + e.getMessage());
         }
         InetAddress clientAddress = datagramPacket.getAddress();
         int clientPort = datagramPacket.getPort();
         Response finalResponse = response;

         sendPool.submit(() -> sendResponse(finalResponse, clientAddress, clientPort));
    }
    private void sendResponse(Response response, InetAddress inetAddress, int port) {
         try {
             byte[] responseData = responseSerializer.serialize(response);
             DatagramPacket responsePacket = new DatagramPacket(responseData, responseData.length, inetAddress, port);
             datagramSocket.send(responsePacket);
         } catch (IOException e) {
             logger.error("Ошибка отправки ответа: {}", e.getMessage());
         }
    }

    /**
     *  Корректно завершает работу сервера.
     */

    public void shutdown() {
        logger.info("Остановка сервера...");
        running = false;
        readPool.shutdownNow();
        processPool.shutdownNow();
        sendPool.shutdownNow();
        if (datagramSocket != null && !datagramSocket.isClosed()) {
            datagramSocket.close();
        }
            logger.info("Работа сервера остановлена");
        }
    }

