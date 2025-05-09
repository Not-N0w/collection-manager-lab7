package com.labs.server.commands;

import java.util.ArrayList;
import java.util.Map;
import java.util.function.Predicate;

import com.labs.common.Command;
import com.labs.common.core.Ticket;
import com.labs.common.exceptions.KeyNotFoundException;
import com.labs.server.CollectionManager;

public class AddCommand implements Command {
    private ArrayList<Ticket> tickets;
    private CollectionManager collectionManager;
    private Predicate<Ticket> addPredicate;

    public AddCommand(CollectionManager collectionManager, Predicate<Ticket> predicate) {
        this.collectionManager = collectionManager;
        this.addPredicate = predicate;
    }

    public Object execute() {
        tickets.stream()
                .filter(addPredicate)
                .forEach(ticket -> collectionManager.add(ticket));
        return null;
    }

    @SuppressWarnings("unchecked")
    public void setArguments(Map<String, Object> data) throws KeyNotFoundException {
        if(!data.containsKey("tickets")) { throw new KeyNotFoundException("tickets"); }
        this.tickets = (ArrayList<Ticket>)data.get("tickets");
    }
}
