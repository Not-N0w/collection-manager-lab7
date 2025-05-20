package com.labs.server;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.labs.common.DataContainer;
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

    private DBManager dbManager;
    /**
     * Конструктор - создание нового объекта.
     */
    public TicketController() {
        dbManager = new DBManager();
        invoker = new Invoker();
    }


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
            dbManager.connect();
            if(data.get("type").equals("command-execution")) {
                invoker.run(data);
                commandResponse = invoker.getResponse();
            }
            else {
                commandResponse = dbManager.processDBRequest(data);
            }
            dbManager.disconnect();
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
