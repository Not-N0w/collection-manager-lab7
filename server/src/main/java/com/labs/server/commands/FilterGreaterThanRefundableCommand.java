package com.labs.server.commands;

import java.util.Comparator;
import java.util.Map;
import java.util.stream.Collectors;

import com.labs.common.Command;
import com.labs.common.DataContainer;
import com.labs.common.core.Ticket;
import com.labs.common.exceptions.KeyNotFoundException;
import com.labs.server.CollectionManager;

public class FilterGreaterThanRefundableCommand implements Command {
    private CollectionManager collectionManager;
    private Boolean refundable;

    public FilterGreaterThanRefundableCommand(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    public DataContainer execute() {
        return collectionManager.filterGreaterThanRefundable(refundable);
    }

    public void setArguments(Map<String, Object> data) throws KeyNotFoundException {
        if (!data.containsKey("refundable")) {
            throw new KeyNotFoundException("refundable");
        }
        this.refundable = (Boolean) data.get("refundable");
    }
}

