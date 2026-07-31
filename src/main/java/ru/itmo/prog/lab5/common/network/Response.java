package ru.itmo.prog.lab5.common.network;

import java.io.Serializable;

/**
 * Упаковывает результат выполнения команды и отправляется от сервера к клиенту.
 */
public class Response implements Serializable {
    private final static long serialVersionUID = 2L;
    private final boolean success;
    private final String message;

    public Response(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    /**
     *
     * @return успешно ли выполнена команда.
     */

    public boolean isSuccess() {
        return success;
    }

    /**
     *
     * @return Сообщение от сервера (результат выполнения или текст ошибки).
     */
    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "Response[" + (success ? "OK" : "ERROR" ) + "]:" + message;
    }
}
