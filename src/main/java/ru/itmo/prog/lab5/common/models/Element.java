package ru.itmo.prog.lab5.common.models;

/**
 * Абстрактный класс для всех элементов коллекции.
 */

public abstract class Element implements Comparable<Element> {
    public abstract Integer getId();
    public abstract void setId(Integer id);
    public abstract String getName();

    public abstract boolean validate();

    @Override
    public int compareTo(Element other) {
        return this.getName().compareTo(other.getName());
    }
}
