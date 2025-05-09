package com.labs.client;

/**
 * Утилитный класс, проверяющий значения данных
 */
public class ValueChecker {

    /** 
     * Конструктор, запрещающий создание объектов класса 
    */
    private ValueChecker() {
    }

    /**
     * Метод, провверяющий входт ли объект в заданные границы
     * 
     * @param <T>       тип входящего объекта
     * @param item      объект для проверки его значения
     * @param limitDown нижняя граница
     * @param limitUp   верхняя граница
     * @param varName   имя поля (для формирования сообщения об ошибке)
     * @throws IllegalArgumentException - если {@code item} не входит в указанные
     *                                  границы
     */
    public static <T extends Number & Comparable<T>> void checkLimits(T item, T limitDown, T limitUp, String varName) {
        String message = varName;
        nullCheck(item, varName);

        if (limitDown == null && limitUp != null) {
            if (item.compareTo(limitUp) >= 0) { // item >= limitUp
                message += " should be less than " + String.valueOf(limitUp);
                throw new IllegalArgumentException(message);
            }
        } else if (limitDown != null && limitUp == null) {
            if (item.compareTo(limitDown) <= 0) { // item <= limitDown
                message += " should be more than " + String.valueOf(limitDown);
                throw new IllegalArgumentException(message);
            }
        } else if (limitDown != null && limitUp != null) {
            if (item.compareTo(limitDown) <= 0 || item.compareTo(limitUp) >= 0) { // item <= limitDown || item >=
                                                                                  // limitUp
                message += " should be less than " + String.valueOf(limitUp) + "and more than "
                        + String.valueOf(limitDown);
                throw new IllegalArgumentException(message);
            }
        }
    }

    /**
     * Метод, проверяющий объект на null
     * 
     * @param <T>     тип входящего объекта
     * @param item    объект для проверки его значения
     * @param varName имя поля (для формирования сообщения об ошибке)
     * @throws IllegalArgumentException - если {@code item} является null
     */
    public static <T> void nullCheck(T item, String varName) {
        if (item == null) {
            String message = varName + " value can't be null!";
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * Метод, проверяющий строку на пустоту
     * 
     * @param item    строка для проверки ее значения
     * @param varName имя поля (для формирования сообщения об ошибке)
     * @throws IllegalArgumentException - если строка {@code item} является пустой
     */
    public static void stringEmptyCheck(String item, String varName) {
        if (item.isEmpty()) {
            String message = varName + " value can't be null!";
            throw new IllegalArgumentException(message);
        }
    }
}