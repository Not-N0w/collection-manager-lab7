package com.labs.server.commands;

import java.util.Map;

import com.labs.common.Command;
import com.labs.common.exceptions.KeyNotFoundException;
import com.labs.server.CollectionManager;

public class RemoveByIdCommand implements Command {
    private CollectionManager collectionManager;
    private Long id;

    public RemoveByIdCommand(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    public Object execute() {
        collectionManager.removeById(id);
        return null;
    }

    public void setArguments(Map<String, Object> data) throws KeyNotFoundException {
        if(!data.containsKey("id")) { throw new KeyNotFoundException("id"); }
        this.id = (Long)data.get("id");
    }
}