package com.labs.common;

import java.util.HashMap;
import java.util.Map;

import com.labs.common.exceptions.KeyNotFoundException;

/**
 * Класс - исполнитель команд
 */
public abstract class AbstractInvoker {
    /**
     * Словарь: имя команды(строка) - Объект {@link Command}
     */
    protected Map<String, Command> commands = new HashMap<>();
    /**
     * Данные, которые вернет команда
     */
    private DataContainer response;
    /**
     * Вызов команды и ее верхнеуровневая обработка
     * @param data содержит команду и ее парраметры
     */
    public void run(DataContainer data) {
        String commandStr = data.getCommand();
        response = new DataContainer(commandStr);
        Command currentCommand = commands.get(commandStr);

        try {
            currentCommand.setArguments(data.fullGet());
        } 
        catch(KeyNotFoundException exception) {
            response.add("status", "error");
            response.add("message", "Command failed: " + exception.getMessage());
            return;
        }

        try {
            response.add("data", currentCommand.execute());
            response.add("status", "ok");
            response.add("message", "Command executed successfully!");
        }
        catch(Exception exception) {
            response.add("status", "error");
            response.add("message", "Command failed: " + exception.getMessage());
            return;
        }
    }
    /**
     * @return ответ последней исполненой команды
     */
    public DataContainer getResponse() {
        return response;
    }

}