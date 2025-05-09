package com.labs.client.localCommandManager;

import com.labs.client.Cycle;
import com.labs.client.DataManager;
import com.labs.client.localCommandManager.commands.ExecuteScriptCommand;
import com.labs.client.localCommandManager.commands.ExitCommand;
import com.labs.client.localCommandManager.commands.HelpCommand;
import com.labs.client.localCommandManager.commands.ConnInfoCommand;
import com.labs.common.AbstractInvoker;

/**
 * Класс - исполнитель команд
 * 
 * @see AbstractInvoker
 */
public class Invoker extends AbstractInvoker {
    /**
     * Конструктор - создание нового объекта + заполнение {@link commands} из
     * {@link AbstractInvoker}
     * 
     * @param cycle       цикл, в котором сейчас находится программа
     * @param dataManager класс обработки данных
     */
    public Invoker(Cycle cycle, DataManager dataManager) {
        commands.put("help", new HelpCommand());
        commands.put("exit", new ExitCommand(cycle));
        commands.put("execute_script", new ExecuteScriptCommand(cycle));
        commands.put("conninfo", new ConnInfoCommand(dataManager));
    }

    /**
     * Метод, проверяющий наличие команды в списке исполняемых.
    */
    public boolean check(String command) {
        return commands.containsKey(command);
    }
}
