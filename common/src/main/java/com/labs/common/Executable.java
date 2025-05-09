package com.labs.common;

/**
 * Интерфейс - указывает, что объект можно исполнить
 */
public interface Executable {
    /**
     * Исполняет объект
     * @return ответ исполнения (null если ответ не предполагается)
     */
    public Object execute();
}