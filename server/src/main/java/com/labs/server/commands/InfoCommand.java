package com.labs.server.commands;

import com.labs.common.Command;
import com.labs.common.DataContainer;
import com.labs.server.CollectionManager;

public class InfoCommand implements Command {
    private CollectionManager collectionManager;

    public InfoCommand(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    public DataContainer execute() {
        return collectionManager.getInfo();
    }
}