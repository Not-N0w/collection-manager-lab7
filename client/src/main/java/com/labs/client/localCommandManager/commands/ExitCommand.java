package com.labs.client.localCommandManager.commands;

import com.labs.client.Cycle;
import com.labs.common.Command;


/**
 * Класс команды exit
 */
public class ExitCommand implements Command {
    /**
     * Поле - текущий цикл
    */
    private Cycle cycle; 
    
    /**
     * Конструктор - создание нового объекта.
     * 
     * @param cycle  текущий цикл
     */
    public ExitCommand(Cycle cycle) {
        this.cycle = cycle;
    }

    /**
     * Метод, исполняющий команду exit, вызывая {@link Cycle#leave()}.
     */
    public Object execute() {
        cycle.leave();
        return null;
    }
}
