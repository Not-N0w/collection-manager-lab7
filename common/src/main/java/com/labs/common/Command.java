package com.labs.common;

import java.util.Map;

import com.labs.common.exceptions.KeyNotFoundException;

/**
 * Интерфейс - команда. Расширяет {@link Executable}
 */
public interface Command extends Executable{
    /**
     * Устанавливает параметры команды
     * 
     * @param data параметры команды в виде словаря
     * @throws KeyNotFoundExeption если нужный параметр в data не найден
     */
    public default void setArguments(Map<String, Object> data) throws KeyNotFoundException {}
}