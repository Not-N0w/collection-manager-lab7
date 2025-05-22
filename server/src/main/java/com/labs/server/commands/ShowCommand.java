package com.labs.server.commands;

import com.labs.common.Command;
import com.labs.common.DataContainer;
import com.labs.common.core.Ticket;
import com.labs.server.CollectionManager;

import java.util.Comparator;
import java.util.stream.Collectors;

public class ShowCommand implements Command {
    private CollectionManager collectionManager;

    public ShowCommand(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    public DataContainer execute() {
        return collectionManager.getAll();
    }
}