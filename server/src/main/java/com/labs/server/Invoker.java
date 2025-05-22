package com.labs.server;

import com.labs.common.AbstractInvoker;
import com.labs.common.core.Ticket;
import com.labs.server.commands.*;

import java.util.ArrayList;

/**
 * Вызыватель команд
 * Расширяет {@link AbstractInvoker}
 */
public class Invoker extends AbstractInvoker {
    private DBManager dbManager;

    public Invoker(DBManager dbManager) {
        this.dbManager = dbManager;
        init();
    }

    /**
     * Конструктор - создание нового объекта.
     */
    public Invoker() {
        init();
    }

    private void init() {
        CollectionManager cm = new CollectionManager(dbManager);
        commands.put("add", new AddCommand(cm, ticket -> true));
        commands.put("update", new UpdateCommand(cm));
        commands.put("show", new ShowCommand(cm));
        commands.put("remove_by_id", new RemoveByIdCommand(cm));
        commands.put("clear", new ClearCommand(cm));
        commands.put("average_of_price", new AverageOfPriceCommand(cm));
        commands.put("count_greater_than_refundable", new CountGreaterThanRefundableCommand(cm));
        commands.put("filter_greater_than_refundable", new FilterGreaterThanRefundableCommand(cm));
        commands.put("add_if_max", new AddCommand(cm, ticket ->
                ((ArrayList<Ticket>)cm.getAll().get("return-data")).stream()
                        .max(Ticket::compareTo)
                        .map(maxTicket -> ticket.compareTo(maxTicket) > 0)
                        .orElse(true)
        ));
        commands.put("add_if_min", new AddCommand(cm, ticket ->
                ((ArrayList<Ticket>)cm.getAll().get("return-data")).stream()
                        .max(Ticket::compareTo)
                        .map(maxTicket -> ticket.compareTo(maxTicket) < 0)
                        .orElse(true)
        ));
        commands.put("remove_greater", new RemoveGreaterCommand(cm));
        commands.put("info", new InfoCommand(cm));
    }

    public void setDbManager(DBManager dbManager) {
        this.dbManager = dbManager;
    }
}
