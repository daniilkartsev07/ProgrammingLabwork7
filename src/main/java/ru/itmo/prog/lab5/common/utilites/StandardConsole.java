package ru.itmo.prog.lab5.common.utilites;

import java.util.Scanner;
import java.util.NoSuchElementException;

public class StandardConsole implements Console {
    private Scanner userScanner = new Scanner(System.in);

    @Override
    public void print(Object obj) {
        System.out.print(obj);

    }

    @Override
    public void println(Object obj) {
        System.out.println(obj);

    }

    @Override
    public String readLine() throws NoSuchElementException, IllegalStateException{
        return userScanner.nextLine();
    }

    @Override
    public boolean isCanRead() {
        return false;
    }

    @Override
    public void printError(Object obj) {
        System.out.println("Error: " + obj);

    }

    @Override
    public void printTable(Object obj1, Object obj2) {
        System.out.printf(" %-30s%-1s%n", obj1, obj2);

    }

    @Override
    public void selectFileScanner(Scanner obj) {
        this.userScanner = obj;

    }

    @Override
    public void selectConsoleScanner(Scanner obj) {
        this.userScanner = new Scanner(System.in);
    }

    public Scanner getUserScanner() {
        return userScanner;
    }

    public void setUserScanner(Scanner userScanner) {
        this.userScanner = userScanner;
    }
}
