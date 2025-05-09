package com.labs.client.localCommandManager.commands;

import java.util.Map;
import com.labs.client.Cycle;
import com.labs.client.Input;
import com.labs.common.Command;
import com.labs.common.exceptions.KeyNotFoundException;

/**
 * Класс команды execute_script
 */
public class ExecuteScriptCommand implements Command {
    /**
     * Поле - путь к скрипту
    */
    private String filePath;

    /**
     * Поле - текущий цикл
    */
    private Cycle cycle;
    String param;
    /**
     * Конструктор - создание нового объекта.
     * 
     * @param cycle  текущий цикл
     */
    public ExecuteScriptCommand(Cycle cycle) {
        this.cycle = cycle;
    }

    /**
     * Метод, исполняющий команду execute_script. Создает новый {@link Cycle} с
     * новым {@link Input} из файла, запрещает комментарии и запускает этот цикл.
     */
    public Object execute() {
        Input input = new Input(cycle.output(), filePath);
        if (!input.checkScanner())
            return null;
        boolean isSilent = false;
        if(param != null && param.equals("s")) isSilent = true;

        Cycle fileCycle = new Cycle(input, cycle.output(), cycle.dataManager(), isSilent);
        fileCycle.output().noComments();
        fileCycle.input().noComments();

        fileCycle.cycle();

        fileCycle.output().allowComments();
        fileCycle.input().allowComments();
        return null;
    }

    /**
     * Метод, устанавливающий аргументы (путь к скрипту в строковом представлении).
     * 
     * @throws KeyNotFoundExeption  если путь не указан в аргументых команды.
     */
    public void setArguments(Map<String, Object> data) throws KeyNotFoundException {
        if (!data.containsKey("path")) {
            throw new KeyNotFoundException("path");
        }
        if(data.containsKey("param")) {
            param = (String) data.get("param");
        }
        this.filePath = (String) data.get("path");
    }
}
