package com.labs.server.commands;

import java.util.Map;

import com.labs.common.Command;
import com.labs.common.core.Ticket;
import com.labs.common.exceptions.KeyNotFoundException;
import com.labs.server.CollectionManager;

public class UpdateCommand implements Command {
    private Ticket ticket;
    private Long id;
    private CollectionManager collectionManager;

    public UpdateCommand(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    public Object execute() {
        collectionManager.update(id, ticket);
        return null;
    }


    @Override
    public void setArguments(Map<String, Object> data) throws KeyNotFoundException {
        if(!data.containsKey("ticket")) { throw new KeyNotFoundException("ticket"); }
        this.ticket = (Ticket)data.get("ticket");

        if(!data.containsKey("id")) { throw new KeyNotFoundException("id"); }
        this.id = (Long)data.get("id");
    }
}
