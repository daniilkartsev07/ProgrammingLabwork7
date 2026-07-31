package ru.itmo.prog.lab5.server.handlers;

import ru.itmo.prog.lab5.common.network.Request;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

/**
 * Класс для десериализации байтов в объект Request.
 * Модуль чтения запросов от клиента.
 */
public class RequestDeserializer {
    /**
     * Десериализирует массив байтов в объект Request.
     * @param data массив байтов, полученных от клиента
     * @return десериализированный объект Request
     */
    public Request deserialize(byte[] data) throws IOException, ClassNotFoundException {
        try (ByteArrayInputStream bis = new ByteArrayInputStream(data);
            ObjectInputStream ois = new ObjectInputStream(bis)) {
            return (Request) ois.readObject();
        }
    }
}
