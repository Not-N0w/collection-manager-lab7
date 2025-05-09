package com.labs.client;

import com.labs.client.extra.Pair;
import com.labs.common.DataContainer;

/**
 * Класс - обработчик данных. Организует отправку запросов и получение ответов с
 * сервера, а так же первичную обработку ответов.
 */
public class DataManager {

    /** 
     * Поле с классом, отвечающим за вывод данных.
    */
    private Output output;
    private Transmitter transmitter;
    /**
     * Конструктор - создание нового объекта.
     * 
     * @param output класс вывода данных
     */
    public DataManager(Output output, Transmitter transmitter) {
        this.output = output;
        this.transmitter = transmitter;
    }
    public String connInfo() {
        return transmitter.connInfo();
    }
    /**
     * Метод, сериализующий данные и отправляющий их на сервер
     * 
     * @param commandData
     * @return true, если зпрос успешно сериализван и отправлен, false - в противном
     *         случае.
     * @see DataContainer
     */
    public boolean send(DataContainer commandData) {
        var connCheck = transmitter.connectionCheck();
        if(connCheck != null) {
            output.responseOut(connCheck);
        }
        DataContainer response = transmitter.send(commandData);
        output.responseOut(response);
        if(response.get("status").equals("error")) return false;
        return true;
    }

    /**
     * @param command комманда (без данных)
     * @param pairs   данные команды представленные в виде пар
     * @return результат метода {@link DataManager#send(DataContainer)}
     * @see Pair
     * @see DataContainer
     */
    public boolean sendCommand(String command, @SuppressWarnings("unchecked") Pair<String, Object>... pairs) {
        DataContainer dataContainer = new DataContainer(command);
        for (var item : pairs) {
            dataContainer.add(item.key(), item.value());
        }
        return send(dataContainer);
    }

    /**
     * Метод получающий ответ с сервера, десериализующий его и возвращающий данные
     * ответа
     * 
     * @return {@link DataContainer} данными ответа
     * @see DataContainer
     */
    public DataContainer getResponse() {
        DataContainer commandResponse = transmitter.getResponse();
        return commandResponse;
    }

    /**
     * Метод получающий ответ с сервера и проверяющий его валидность
     * 
     * @see DataContainer
     */
    public void processResponse(boolean isSilent) {
        var response = getResponse();
        if(isSilent) return;
        output.responseOut(response);
    }
}
