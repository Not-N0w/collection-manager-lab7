package com.labs.server;

import java.io.IOException;
import java.util.ArrayList;

import com.labs.common.DataContainer;
import com.labs.common.core.Ticket;
import com.labs.common.dataConverter.Deserializer;
import com.labs.common.dataConverter.Serializer;

/**
 * Класс - распределитель на серверной части
 */
public class TicketController {

    /**
     * Вызыватель команды
     */
    private Invoker invoker;
    private FileManager fileManager;
    /**
     * Конструктор - создание нового объекта.
     */
    public TicketController() {
        fileManager = new FileManager("/server/saved_dir/saved");
        invoker = new Invoker();
    }

    public void loadTickets() {
        DataContainer dataContainer = new DataContainer();
        dataContainer.setCommad("add");
        dataContainer.add("tickets", fileManager.getTickets());
        invoker.run(dataContainer);
    }
    public void saveTickets() {
        invoker.run(new DataContainer("show"));
        ArrayList<Ticket> resp = (ArrayList<Ticket>) invoker.getResponse().get("data");
        fileManager.saveTickets(resp);
    }
    /**
     * Обработка данных
     * 
     * @param inData данные с клиента в байтовом представлении
     */
    public byte[] process(byte[] inData) {
        DataContainer data = null;
        DataContainer commandResponse = new DataContainer();
        commandResponse.add("from", "SERVER");

        boolean skipExecution = false;
        try {
            data = Deserializer.deserialize(inData);
        } catch (IOException exception) {
            commandResponse.add("status", "error");
            commandResponse.add("message", "Deserialization error (IO).");
            skipExecution = true;

        } catch (ClassNotFoundException exception) {
            commandResponse.add("status", "error");
            commandResponse.add("message", "Deserialization error. Invalid class.");
            skipExecution = true;
        }
        if(!skipExecution) {
            invoker.run(data);
            commandResponse = invoker.getResponse();
        }

        byte[] outData;
        try {
            outData = Serializer.serialize(commandResponse);
            return outData;
        } catch (IOException exception) {
            // idk mb try again or sth else
        }
        return null;
    }
}
