package ru.itmo.prog.lab5.common.utilites;
import ru.itmo.prog.lab5.common.models.*;
import java.util.Scanner;
import java.util.NoSuchElementException;

/**
 * Класс для интерактивного и скриптового создания объектов
 */

public class Asker {
    private final Scanner scanner;
    private final boolean isScriptMode;

    public Asker(Scanner scanner) {
        this.scanner = scanner;
        this.isScriptMode = false;
    }
    public Asker(Scanner scanner, boolean isScriptMode) {
        this.scanner = scanner;
        this.isScriptMode = isScriptMode;
    }

    /**
     * Метод для чтения строки с проверкой на null/empty.
     */

    public String askString(String message, boolean canBeNull) {
        while (true) {
            if (!isScriptMode) {
                System.out.println(message);
            } if (!scanner.hasNextLine()) {
                if (isScriptMode) {
                    throw new NoSuchElementException("Ошибка: закончились данные в скрипте!");
                }

                System.out.println("\nВвод прерван.");
                System.exit(0);
            }
            try {
                String input = scanner.nextLine().trim();

                if (!canBeNull && input.isEmpty()) {
                    if (isScriptMode) {
                        throw new IllegalArgumentException("Ошибка скрипта: поле не может быть пустым!");
                    }
                    System.out.println("Ошибка: поле не может быть пустым!");
                    continue;
                }
                    return input.isEmpty() ? null : input;

                } catch (NoSuchElementException e) {
                if (isScriptMode) {
                    throw new NoSuchElementException("Данные скрипта закончились во время чтения строки.");
                }
                System.out.println("Ошибка чтения ввода!");
                System.exit(0);
            }
        }
    }

    /**
     * Чтение Long с проверкой границ.
     */

    public Long askLong(String message, Long min, Long max, boolean canBeNull) {
        while (true) {
            String input = askString(message, canBeNull);
            if (input == null) {
                return null;
            }
            try {
                long val = Long.parseLong(input);
                if ((min != null && val <= min) || (max != null && val > max)) {
                    if (isScriptMode) {
                        throw new IllegalArgumentException("Ошибка скрипта: число вне числового диапазона (" + min + ";" + max + ")");
                    }
                    System.out.println("Ошибка: число должно быть в диапазоне (" + min + ";" + max + "]");
                    continue;
                }
                return val;
            } catch (NumberFormatException e) {
                if (isScriptMode) {
                    throw new IllegalArgumentException("Ошибка скрипта: ожидалось целое число, а получено '" + input + "'");
                }
                System.out.println("Ошибка: введите целое число!");
            }
        }
    }

    /**
     * Чтение Double.
     */

    public Double askDouble(String message, boolean canBeNull) {
        while (true) {
            String input = askString(message, canBeNull);
            if (input == null) {
                return null;
            }
            try {
                return Double.parseDouble(input.replace(",","."));
            } catch (NumberFormatException e) {
                if (isScriptMode) {
                    throw new IllegalArgumentException("Ошибка скрипта: ожидается число с плавающей точкой, получено '" + input + "'");
                }
                System.out.println("Ошибка: введите число (допускается наличие точки).");
            }
        }
    }

    /**
     * Чтение Enum.
     */

    public <T extends Enum <T>> T askEnum(String message, Class<T> enumClass, boolean canBeNull) {
        String list = java.util.Arrays.toString(enumClass.getEnumConstants());
        while (true) {
            if (!isScriptMode) {
                System.out.println("Доступные варианты: " + list);
            }
            String input = askString(message, canBeNull);
            if (input == null) {
                return null;
            }
            try {
                return Enum.valueOf(enumClass, input.toUpperCase());
            } catch (IllegalArgumentException e) {
                if (isScriptMode) {
                    throw new IllegalArgumentException("Ошибка скрипта: значения '" + input + " нет в Enum " + enumClass.getSimpleName());
                }
                System.out.println("Ошибка: такого варианта нет в списке!");
            }
        }
    }

    /**
     * Сборка объекта Координат.
     */

    public Coordinates askCoordinates() {
        if (!isScriptMode) {
            System.out.println("--- Ввод координат ---");
        }
            Long x = askLong("Введите X (Long, максимальное значение - 688): ", null, 688L, false);
            Integer y = (int)(long) askLong("Введите Y (Integer): ", null, null, false);
            return new Coordinates(x, y);
        }

    /**
     * Сборка объекта Локации.
     */

        public Location askLocation() {
        if (!isScriptMode) {
            System.out.println("--- Ввод локации ---");
        }
        Integer x = (int)(long) askLong("Введите X локации (Integer): ", null, null, false);
        int y = (int)(long) askLong("Введите Y локации (int): ", null, null, false);
        Double z = askDouble("Введите Z локации (Double): ", false);

        String name;
        while (true) {
            name = askString("Введите название локации (максимальное количество символов - 315): ", false);
            if (name.length() > 315) {
                if (isScriptMode) {
                    throw new IllegalArgumentException("Ошибка скрипта: имя локации > 315 символов");
                }
                System.out.println("Ошибка: длина названия не должна превышать 315 символов!");
                continue;
            }
            break;
        }
        return new Location(x,y,z,name);
        }

    /**
     * Сборка объекта Убийцы.
     */

        private Person askPerson() {
        if (!isScriptMode) {
            System.out.println("--- Ввод данных убийцы ---");
        }
        String personName = askString("Введите имя человека: ", false);
        Double height = askDouble("Введите рост (>0): ", false);
        while (height != null && height <= 0) {
            if (isScriptMode) {
                throw new IllegalArgumentException("Ошибка скрипта: рост должен быть > 0");
            }
            System.out.println("Ошибка: рост должен быть больше 0!");
            height = askDouble("Введите рост (>0): ", false);
        }
        Location location = null;
        if (askString("Указать локацию человека? (Enter - нет, любой текст - да): ", true) != null) {
            location = askLocation();
        }
        return new Person(personName, height, location);
        }

    /**
     * Сборка объекта Дракона.
     */

    public Dragon createDragon() {
        String name = askString("Введите имя дракона: ", false);
        Coordinates coordinates = askCoordinates();
        Long age = askLong("Введите возраст (Long, >0): ", 0L, null, true);
        Color color = askEnum("Введите цвет: ", Color.class, false);
        DragonType type = askEnum("Выберите тип дракона: ", DragonType.class, false);
        DragonCharacter character = askEnum("Выберите характер: ", DragonCharacter.class, false);
        Person killer = null;
        if (askString("У дракона есть убийца? (Enter - нет, любой текст - да): ", true) != null) {
            killer = askPerson();
        }
        return new Dragon(name, coordinates, age, color, type, character, killer);
        }
}

