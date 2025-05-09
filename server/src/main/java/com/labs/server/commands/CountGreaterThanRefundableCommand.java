package com.labs.server.commands;

import java.util.Map;

import com.labs.common.Command;
import com.labs.common.exceptions.KeyNotFoundException;
import com.labs.server.CollectionManager;

public class CountGreaterThanRefundableCommand implements Command {
    private CollectionManager collectionManager;
    private Boolean refundable;

    public CountGreaterThanRefundableCommand(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    public Object execute() {
        return collectionManager.getAll().stream()
                .filter(ticket -> ticket.refundable() == true && refundable == false)
                .count();
    }

    public void setArguments(Map<String, Object> data) throws KeyNotFoundException {
        if(!data.containsKey("refundable")) { throw new KeyNotFoundException("refundable"); }
        this.refundable = (Boolean)data.get("refundable");
    }
}