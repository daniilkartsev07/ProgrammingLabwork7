package ru.itmo.prog.lab5.common.models;
import ru.itmo.prog.lab5.common.utilites.Validatable;

import java.io.Serializable;


public class Coordinates implements Validatable, Serializable {
    private Long x;
    private Integer y;
    public Coordinates(Long x, Integer y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Проверяет валидность полей
     * @return true, если все верно, иначе false.
     */

    @Override
    public boolean validate() {
        if (x == null || x > 688) {
            return false;
        }
        if (y == null) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Coordinates{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }

    public Long getX() {
        return x;
    }

    public Integer getY() {
        return y;
    }
    public String toCSV() {
        return x + ";" + y;
    }
}
