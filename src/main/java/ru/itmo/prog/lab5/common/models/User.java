package ru.itmo.prog.lab5.common.models;
import java.io.Serializable;

/**
 * Класс пользователя.
 */

public class User implements Serializable {
    private static final long serialVersionUID = 1L;
    private String name;
    private String login;
    private Integer id;
    private String password;

    public User(String login, Integer id, String name, String password) {
        this.login = login;
        this.id = id;
        this.name = name;
        this.password = password;
    }

    public String getLogin() {
        return login;
    }

    public Integer getId() {
        return id;
    }


    public String getName() {
        return name;
    }


    public String getPassword() {
        return password;
    }
    @Override
    public String toString() {
        return "User{ + " +
                "id= " + id +
                ", name='" + name + '\'' +
                ", password='*********'" +
                '}';
    }
}
