package ru.itmo.prog.lab5.common.models;

import java.io.Serializable;
import java.time.LocalDate;

public class Dragon extends Element implements Serializable {
    private Integer id;
    private String name;
    private Coordinates coordinates;
    private java.time.LocalDate creationDate;
    private Long age;
    private Long ownerId;
    private Color color;
    private DragonType type;
    private DragonCharacter character;
    private Person killer;

    private static Integer nextId = 1;

    public Dragon(String name, Coordinates coordinates, Long age, Color color, DragonType type, DragonCharacter character, Person killer) {
        this.id = nextId++;
        this.name = name;
        this.coordinates = coordinates;
        this.age = age;
        this.color = color;
        this.type = type;
        this.character = character;
        this.killer = killer;
        this.creationDate = java.time.LocalDate.now();
    }

    public Dragon(Integer id, String name, Coordinates coordinates, java.time.LocalDate creationDate, Long age, Color color, DragonType type, DragonCharacter character, Person killer) {
        this.id = id;
        this.name = name;
        this.coordinates = coordinates;
        this.creationDate = creationDate;
        this.age = age;
        this.color = color;
        this.type = type;
        this.character = character;
        this.killer = killer;
    }

    @Override
    public boolean validate() {
        if (id == null || id <= 0) return false;
        if (name == null || name.isBlank()) return false;
        if (coordinates == null) return false;
        if (creationDate == null) return false;
        if (age != null && age <= 0) return false;
        if (color == null || character == null) return false;
        return true;
    }

    @Override
    public int compareTo(Element other) {
        return this.name.compareTo(other.getName());
    }

    @Override
    public Integer getId() { return id; }

    @Override
    public void setId(Integer id) { this.id = id; }

    @Override
    public String getName() { return name; }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public LocalDate getCreationDate() {
        return creationDate;
    }

    public Long getAge() {
        return age;
    }

    public Color getColor() {
        return color;
    }

    public DragonType getType() {
        return type;
    }

    public DragonCharacter getCharacter() {
        return character;
    }

    public Person getKiller() {
        return killer;
    }

    public static Integer getNextId() {
        return nextId;
    }

    public String toCSV() {
        return id + ";" + name + ";" + coordinates.getX() + ";" + coordinates.getY() + ";" +
                creationDate + ";" + (age == null ? "" : age) + ";" + color + ";" +
                (type == null ? "" : type) + ";" + character + ";" +
                (killer == null ? "null" : killer.toCSV());
    }

    @Override
    public String toString() {
        return "Dragon{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", coordinates=" + coordinates +
                ", creationDate=" + creationDate +
                ", age=" + age +
                ", color=" + color +
                ", type=" + type +
                ", character=" + character +
                ", killer=" + killer +
                '}';
    }
    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }
}

