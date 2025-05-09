package com.labs.client.localCommandManager.commands;

import com.labs.client.Cycle;
import com.labs.common.Command;
import com.labs.client.DataManager;


public class ConnInfoCommand implements Command {

    private DataManager dataManager;

    public ConnInfoCommand(DataManager dataManager) {
        this.dataManager = dataManager;
    }

    public Object execute() {
        return dataManager.connInfo();
    }
}
