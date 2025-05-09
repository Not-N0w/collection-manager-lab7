package com.labs.common.core;

/**
 * Интерфейс - показывет, что поле объекта может быть установлено по
 * наименованию поля
 */
@FunctionalInterface
public interface Settable {
    /**
     * Устанавливает значение поля объекта по наименованию.
     * 
     * @param <T>       тип устанавливаемого поля
     * @param fieldName наименование поля
     * @param in        значение, которое необходимо установить
     * @throws IllegalArgumentException если поле с наименованием {@link fieldName}
     *                                  не найдено в объекте
     */
    public <T> void set(String fieldName, T in) throws IllegalArgumentException;
}
