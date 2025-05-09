package com.labs.server.commands;

import com.labs.common.Command;
import com.labs.server.CollectionManager;

public class ClearCommand implements Command {
    private CollectionManager collectionManager;

    public ClearCommand(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    public Object execute() {
        collectionManager.clear();
        return null;
    }
}