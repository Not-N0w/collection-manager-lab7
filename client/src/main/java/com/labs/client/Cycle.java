package com.labs.client;

import java.util.NoSuchElementException;

import com.labs.client.localCommandManager.CommandManager;
import com.labs.common.DataContainer;

/** 
 * Класс с реализаций цикла приложения
 */
public class Cycle {
    /** 
     * Менеджер команд для исполнения клиентских команд 
    */
    private CommandManager localCommandManager;

    /** 
     * Поле с классом, отвечающим за получение данных от пользователя.
    */
    private Input input;

    /** 
     * Поле с классом, отвечающим за вывод данных. 
    */
    private Output output;

    /** 
     * Поле с классом, отвечающим за обработку данных. 
    */
    private DataManager dataManager;

    /** 
     * Флаг, указывающий на необходимость выхода из цикла 
    */
    private boolean needLeave = false;
    private boolean isSilent = false;
    /**
     * Конструктор создание нового объекта.
     * 
     * @param input       класс ввода данных
     * @param output      класс вывода данных
     * @param dataManager класс обработки данных
     */
    public Cycle(Input input, Output output, DataManager dataManager, boolean isScilent) {
        localCommandManager = new CommandManager(this, dataManager);
        this.input = input;
        this.output = output;
        this.dataManager = dataManager;
        this.isSilent = isScilent;
    }

    /**
     * Метод устанавливающий, флаг выхода на true
     */
    public void leave() {
        needLeave = true;
    }

    /**
     * Метод с основным циклом: ожидание команды, парсинг данных, запрос отправки на
     * сервер, получение ответа, обработка и вывод ответа.
     */
    public void cycle() {
        while (!needLeave) {
            if(!isSilent) {
                output.waiting();
            }
            String command = "";
            try {
                command = input.getCommand();
            } catch (NoSuchElementException exception) {
                return;
            }

            DataContainer commandData = input.getData(command);
            if (commandData == null)
                continue;

            DataContainer localResponse = localCommandManager.executeCommand(commandData);
            if (localResponse != null) {
                if(!isSilent) {
                    output.responseOut(localResponse);
                }
                continue;
            }

            if(!dataManager.send(commandData)) continue;

            dataManager.processResponse(isSilent);

        }
    }

    /**
     * @return DataManager
     */
    public DataManager dataManager() {
        return this.dataManager;
    }

    /**
     * @return Input
     */
    public Input input() {
        return this.input;
    }

    /**
     * @return Output
     */
    public Output output() {
        return this.output;
    }

}
