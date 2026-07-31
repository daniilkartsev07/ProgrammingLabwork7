package ru.itmo.prog.lab5.common.models;
import ru.itmo.prog.lab5.common.utilites.Validatable;

import java.io.Serializable;

/**
 * Класс локации.
 */

public class Location implements Validatable, Serializable {
    private Integer x;
    private int y;
    private Double z;
    private String name;

    public Location(Integer x, int y, Double z, String name) {
        this.name = name;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public boolean validate() {
        if (x == null) {
            return false;
        } if (z == null) {
            return false;
        } if (name != null && name.length() > 315) {
            return false;
        } if (name == null) {
            return false;
        }
        return true;
    }

    public String getName() {
        return name;
    }

    public Integer getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Double getZ() {
        return z;
    }

    @Override
    public String toString() {
        return "Location{" +
                "x=" + x + ", y=" + y + ", z=" + z + ", name=" + name + '\'' +
                "}";
    }

    public String toCSV() {
        return x + ";" + y + ";" + z + ";" + name;
    }
}
