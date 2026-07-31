package ru.itmo.prog.lab5.common.utilites;

import java.util.Scanner;

public interface Console {
    void print(Object obj);
    void println(Object obj);
    String readLine();
    boolean isCanRead();
    void printError(Object obj);
    void printTable(Object obj1, Object obj2);
    void selectFileScanner(Scanner obj);
    void selectConsoleScanner(Scanner obj);
}
