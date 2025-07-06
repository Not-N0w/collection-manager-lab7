package com.labs.server.commands;

import com.labs.common.Command;
import com.labs.common.DataContainer;
import com.labs.server.CollectionManager;

public class ClearCommand implements Command {
    private CollectionManager collectionManager;

    public ClearCommand(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    public DataContainer execute() {
        return collectionManager.clear();
    }
}