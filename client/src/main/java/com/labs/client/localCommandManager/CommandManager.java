package com.labs.client.localCommandManager;

import com.labs.client.Cycle;
import com.labs.client.DataManager;
import com.labs.client.UserManager;
import com.labs.common.DataContainer;

/**
 * Класс - менеджер команд
*/
public class CommandManager {
    /** Поле - исполнитель команды */
    private Invoker invoker;

    /**
     * Конструктор - создание нового объекта.
     * 
     * @param cycle       цикл, в котором сейчас находится программа
     * @param dataManager класс обработки данных
     */
    public CommandManager(Cycle cycle, DataManager dataManager, UserManager userManager) {
        invoker = new Invoker(cycle, dataManager, userManager);
    }

    /**
     * Метод, запускающий исполнение команды
     * 
     * @param dataContainer данные команды
     * @return ответ исполнителя
     */
    public DataContainer executeCommand(DataContainer dataContainer) {
        if (!invoker.check(dataContainer.getCommand()))
            return null;
        invoker.run(dataContainer);

        var response = invoker.getResponse();
        response.add("from", "CLIENT");
        return response;
    }
}
