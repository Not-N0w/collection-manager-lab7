package com.labs.server.commands;

import java.util.ArrayList;
import java.util.Map;
import java.util.function.Predicate;

import com.labs.common.Command;
import com.labs.common.DataContainer;
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

    public DataContainer execute() {
        DataContainer dataContainer = new DataContainer();
        ArrayList<DataContainer> responseList = new ArrayList<>();
        tickets.stream()
                .filter(addPredicate)
                .forEach(ticket -> responseList.add(collectionManager.add(ticket)));

        var count = responseList.stream()
                .filter(response -> response.get("status").equals("ok"))
                .count();
        dataContainer.add("message", count + " tickets added");
        dataContainer.add("status", "ok");
        return dataContainer;
    }

    @SuppressWarnings("unchecked")
    public void setArguments(Map<String, Object> data) throws KeyNotFoundException {
        if(!data.containsKey("tickets")) { throw new KeyNotFoundException("tickets"); }
        this.tickets = (ArrayList<Ticket>)data.get("tickets");

        var userId = (Long)data.get("User-id");
        for(Ticket ticket : this.tickets) {
            ticket.setOwnerId(userId);
        }
    }
}
