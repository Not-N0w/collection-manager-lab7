package com.labs.common.exceptions;

/**
 * Исключение - если наименование поля не найдено в классе
 */
public class KeyNotFoundException extends Exception {
    /**
     * Конструктор - создание нового исключения
     * 
     * @param key наименование поля, которе не найдено в классе.
     */
    public KeyNotFoundException(String key) {
        super("Key " + key + " not found.");
    }
}
