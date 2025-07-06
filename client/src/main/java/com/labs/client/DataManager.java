package com.labs.client;

import com.labs.client.extra.Pair;
import com.labs.common.DataContainer;
import com.labs.common.user.User;

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
    private boolean noComments = false;
    /**
     * Конструктор - создание нового объекта.
     * 
     * @param output класс вывода данных
     */
    public DataManager(Output output, Transmitter transmitter) {
        this.output = output;
        this.transmitter = transmitter;
    }
    public void nextSilent() { noComments = true; }
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
    public boolean send(DataContainer commandData, User user) {
        var connCheck = transmitter.connectionCheck();
        DataContainer response = transmitter.send(commandData, user);
        if(!noComments) { output.responseOut(response); }
        noComments = false;
        if(response.get("status").equals("error")) return false;
        return true;
    }

    /**
     * @param command комманда (без данных)
     * @see Pair
     * @see DataContainer
     */
    public boolean sendCommand(String command, User user, @SuppressWarnings("unchecked") Pair<String, Object>... pairs) {
        DataContainer dataContainer = new DataContainer(command);
        for (var item : pairs) {
            dataContainer.add(item.key(), item.value());
        }
        return send(dataContainer, user);
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
