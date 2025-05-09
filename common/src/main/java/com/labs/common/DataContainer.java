package com.labs.common;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Контейнер, предназначенный для общения клиента и сервера
 */
public class DataContainer implements Serializable {
    /**
     * Данные, которые контейнер содержит
     */
    private Map<String, Object> data;
    /**
     * Команда в строковом представлении
     */
    private String command;
    private static final long serialVersionUID = 1L;

    /**
     * Конструктор - создание нового объекта
     * 
     * @see DataContainer#DataContainer(String)
     */
    public DataContainer() {
        data = new HashMap<>();
    }

    /**
     * Конструктор - создание нового объекта c определенной командой
     * 
     * @see DataContainer#DataContainer(String)
     */
    public DataContainer(String command) {
        this();
        this.command = command;
    }

    /**
     * Добавить пару ключ-значение в контейнер
     * 
     * @param key   ключ
     * @param value значение
     */
    public void add(String key, Object value) {
        data.put(key, value);
    }

    /**
     * Получить данные по ключу из контейнера
     * 
     * @param <T> тип данных, которые ожидаются
     * @param key ключ
     * @return данные по ключю приведенные к типу T
     */
    public <T> T get(String key) {
        try {
            @SuppressWarnings("unchecked")
            T value = (T) data.get(key);
            return value;
        } catch (ClassCastException exception) {
            return null;
        }
    }

    /**
     * @return все данные из контейнера
     */
    public Map<String, Object> fullGet() {
        return this.data;
    }

    /**
     * Задает строковое представление команды
     * 
     * @param command строковое представление команды
     */
    public void setCommad(String command) {
        this.command = command;
    }

    /**
     * @return строковое представление команды
     */
    public String getCommand() {
        return command;
    }

}
