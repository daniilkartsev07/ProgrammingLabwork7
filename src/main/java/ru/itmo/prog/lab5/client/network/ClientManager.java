package ru.itmo.prog.lab5.client.network;

import ru.itmo.prog.lab5.common.network.Request;
import ru.itmo.prog.lab5.common.network.Response;



/**
 * Интерфейс, отвечающий за отправку запросов на сервер и получение ответов.
 */

public interface ClientManager {
    Response sendRequest(Request request);
}
