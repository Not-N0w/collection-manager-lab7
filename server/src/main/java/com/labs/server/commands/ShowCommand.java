package com.labs.server.commands;

import com.labs.common.Command;
import com.labs.common.core.Ticket;
import com.labs.server.CollectionManager;

import java.util.Comparator;
import java.util.stream.Collectors;

public class ShowCommand implements Command {
    private CollectionManager collectionManager;

    public ShowCommand(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    public Object execute() {
        return collectionManager.getAll().stream().sorted(Comparator.comparing(Ticket::name)).collect(Collectors.toList());
    }
}