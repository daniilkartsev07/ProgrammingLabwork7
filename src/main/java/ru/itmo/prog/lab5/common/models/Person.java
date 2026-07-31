package ru.itmo.prog.lab5.common.models;
import ru.itmo.prog.lab5.common.utilites.Validatable;

import java.io.Serializable;

/**
 * Класс человека (убийцы драконов).
 */

public class Person implements Validatable, Serializable {
    private String name;
    private Double height;
    private Location location;

    public Person(String name, Double height, Location location) {
        this.name = name;
        this.height = height;
        this.location = location;
    }

    @Override
    public boolean validate() {
        if (name == null || name.isBlank()) {
            return false;
        } if (height == null || height <= 0) {
            return false;
        } if (location == null) {
            return false;
        }
        return true;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setHeight(Double height) {
        this.height = height;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Double getHeight() {
        return height;
    }

    public Location getLocation() {
        return location;
    }

    @Override
    public String toString() {
        return "ru.itmo.prog.lab5.common.models.Person{" + "name=" + name + '\''
                + ", height=" + height + ", location=" + location +
                "}";
    }
    public String toCSV() {
        String locationData = (location == null) ? ";;;" : location.toCSV();
        return name + ";" + height + ";" + locationData;
    }
}
