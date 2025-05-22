package com.labs.client.localCommandManager.commands;

import com.labs.client.Cycle;
import com.labs.common.Command;
import com.labs.client.DataManager;
import com.labs.common.DataContainer;


public class ConnInfoCommand implements Command {

    private DataManager dataManager;

    public ConnInfoCommand(DataManager dataManager) {
        this.dataManager = dataManager;
    }

    public DataContainer execute() {
        DataContainer dataContainer = new DataContainer();
        dataContainer.add("status", "ok");
        dataContainer.add("return-data", dataManager.connInfo());
        return dataContainer;
    }
}
