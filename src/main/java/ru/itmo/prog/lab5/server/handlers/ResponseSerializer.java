package ru.itmo.prog.lab5.server.handlers;

import ru.itmo.prog.lab5.common.network.Response;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

/**
 * Класс для сериализации объекта Response в массив байтов.
 * Модуль отправки ответов клиенту.
 */
public class ResponseSerializer {
    /**
     * Сериализует объект Response в массив байтов для отправки клиенту.
     * @param response объект ответа для сериализации
     * @return массив байтов, готовый к отправке
     */
    public byte[] serialize(Response response) throws IOException {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos)) {
            oos.writeObject(response);
            oos.flush();
            return bos.toByteArray();
        }
    }
}
