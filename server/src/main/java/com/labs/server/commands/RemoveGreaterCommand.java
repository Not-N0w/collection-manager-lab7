package com.labs.server.commands;

import java.util.Map;

import com.labs.common.Command;
import com.labs.common.core.Ticket;
import com.labs.common.exceptions.KeyNotFoundException;
import com.labs.server.CollectionManager;

public class RemoveGreaterCommand implements Command {
    private Ticket ticket;
    private CollectionManager collectionManager;

    public RemoveGreaterCommand(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    public Object execute() {
        collectionManager.removeGreater(ticket);
        return null;
    }

    public void setArguments(Map<String, Object> data) throws KeyNotFoundException {
        if(!data.containsKey("ticket")) { throw new KeyNotFoundException("ticket"); }
        this.ticket = (Ticket)data.get("ticket");
    }
}
