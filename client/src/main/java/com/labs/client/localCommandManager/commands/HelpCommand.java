package com.labs.client.localCommandManager.commands;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import com.labs.common.Command;
import com.labs.common.DataContainer;

/**
 * Класс ккоманды help
*/
public class HelpCommand implements Command {

    /**
     * Метод, исполняющий команду help. Возвращяает все из файла help.txt в
     * строковом представлении.
     */
    public DataContainer execute() {
        DataContainer dataContainer = new DataContainer();
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("help.txt");
        if (inputStream == null) {
            dataContainer.add("status", "error");
            dataContainer.add("message", "Something wrong with help file");
            return dataContainer;
        }
        Scanner scanner = new Scanner(inputStream, StandardCharsets.UTF_8);
        String data = scanner.useDelimiter("\\A").next();
        scanner.close();
        dataContainer.add("status", "ok");
        dataContainer.add("message", "Help command executed successfully");
        dataContainer.add("return-data", data);
        return dataContainer;
    }
}
