package com.labs.server;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import com.labs.common.DataContainer;
import com.labs.common.core.Ticket;

/**
 * Контролирует коллекцию
 */
public class CollectionManager {
    /**
     * Коллекция
     */
    private static volatile TreeSet<Ticket> treeSet;

    private DBManager dbManager;
    /**
     * Конструктор - создание нового объекта
     */
    public CollectionManager(DBManager dbManager) {
        this.dbManager = dbManager;
    }

    public static void sync() {
        DBManager dbManager = new DBManager();
        dbManager.connect();
        treeSet = dbManager.getCollection();
        dbManager.disconnect();
    }
    /**
     * Добавляет объект в колекцию
     * 
     * @param ticket этот объект нужно добавить в колекцию
     */
    public DataContainer add(Ticket ticket) {
        DataContainer data = new DataContainer();

        boolean isAdded = dbManager.addTicket(ticket);
        if(!isAdded) {
            data.add("status", "error");
            data.add("message", "Database error. 0 tickets added");
            return data;
        }

        treeSet.add(ticket);

        data.add("status", "ok");
        data.add("message", "1 ticket added");
        return data;
    }

    /**
     * Изменяет объект по id
     * 
     * @param id     id объекта в коллекции
     * @param ticket на что заменяем
     */
    public DataContainer update(Long id, Ticket ticket) {

        DataContainer data = new DataContainer();

        try {
            dbManager.updateTicket(id, ticket);
        } catch (Exception e) {
            data.add("status", "error");
            data.add("message", e.getMessage());
            return data;
        }

        Ticket toRemove = treeSet.stream()
                .filter(t -> t.id().equals(id))
                .findFirst()
                .orElse(null);

        if (toRemove != null) {
            treeSet.remove(toRemove);
        }
        ticket.setId(id);
        treeSet.add(ticket);
        data.add("message", "1 ticket updated");
        return data;
    }

    /**
     * @return массив всех объектов коллекции
     */
    public DataContainer getAll() {

        DataContainer data = new DataContainer();
        ArrayList<Ticket> tickets = new ArrayList<>(treeSet);

        data.add("status", "ok");
        data.add("message", tickets.size() + " tickets received");
        data.add("return-data", tickets);
        return data;

    }

    /**
     * Удаляет объект из коллекции по его id
     * 
     * @param id id удаляемого объекта
     */
    public DataContainer removeById(Long id) {
        DataContainer data = new DataContainer();

        try {
            dbManager.deleteTicket(id);
        } catch (Exception e) {
            data.add("status", "error");
            data.add("message", e.getMessage());
            return data;
        }
        treeSet.removeIf(ticket -> (ticket.id() == id));
        data.add("message", "1 ticket removed");
        return data;
    }

    /**
     * Очищает коллекцию
     */
    public DataContainer clear() {
        DataContainer data = new DataContainer();

        for(Ticket ticket : treeSet) {
            if(ticket.getOwnerId() != dbManager.getUserId()) {
                data.add("status", "error");
                data.add("message", "Permission denied");
                return data;
            }
        }
        try {
            dbManager.clear();
        } catch (Exception e) {
            data.add("status", "error");
            data.add("message", e.getMessage());
            return data;
        }

        int size = treeSet.size();
        treeSet.clear();
        data.add("status", "ok");
        data.add("message", size + " tickets cleared");
        return data;
    }

    /**
     * Ищет среднюю цену по объектам коллекции
     * 
     * @return средняя цена билетов в коллекции
     */
    public DataContainer averageOfPrice() {
        DataContainer data = new DataContainer();
        if (treeSet.isEmpty()) {
            data.add("message", "No tickets found");
            return data;
        }
        AtomicInteger sumPrice = new AtomicInteger(0);
        treeSet.forEach(x -> sumPrice.addAndGet(x.price()));
        Integer avg = sumPrice.get() / treeSet.size();

        data.add("message", "Avg counted");
        data.add("return-data", avg);
        return data;
    }

    /**
     * Приводит Boolean к Int (1 = true, 0 = false)
     * 
     * @param in входное Boolean значение
     * @return int (1 или 0)
     */
    private int booleanToInt(Boolean in) {
        return (in.equals(Boolean.valueOf(true)) ? 1 : 0);
    }

    /**
     * Считает количество объектов, значение поля refundable у которых больше
     * заданного
     * 
     * @param refundable входное значение refundable
     * @return количество элементов коллекции, подходящих под условие
     */
    public DataContainer countGreaterThanRefundable(Boolean refundable) {
        DataContainer data = new DataContainer();
        Long count = treeSet.stream()
                .filter(item -> booleanToInt(item.refundable()) > booleanToInt(refundable)) // absurdly, but according
                                                                                            // to TOR
                .count();
        data.add("status", "ok");
        data.add("message", "Counted greater than refundable");
        data.add("return-data", count);
        return data;
    }

    /**
     * Возвращает элементы коллекции, значение поля refundable у которых больше
     * заданного
     * 
     * @param refundable входное значение refundable
     * @return элементы коллекции, подходящие под условие
     */
    public DataContainer filterGreaterThanRefundable(Boolean refundable) {
        DataContainer data = new DataContainer();
        ArrayList<Ticket> tickets = (ArrayList<Ticket>) treeSet.stream()
                .filter(item -> booleanToInt(item.refundable()) > booleanToInt(refundable)) // absurdly, but according
                                                                                            // to TOR
                .collect(Collectors.toCollection(ArrayList::new));
        data.add("status", "ok");
        data.add("message", tickets.size() + " tickets filtered");
        data.add("return-data", tickets);
        return data;
    }

    /**
     * Добавляет ticket, если он больше всех элементов в коллекции
     * 
     * @param ticket входной ticket
     */
    public DataContainer addIfMax(Ticket ticket) {
        DataContainer data = new DataContainer();
        if (ticket.compareTo(treeSet.last()) > 0) {

            boolean isAdded = dbManager.addTicket(ticket);
            if(!isAdded) {
                data.add("status", "error");
                data.add("message", "Database error. 0 tickets added");
                return data;
            }

            treeSet.add(ticket);
            data.add("status", "ok");
            data.add("message", "1 ticket added");
            return data;
        }
        data.add("status", "ok");
        data.add("message", "0 ticket added");
        return data;
    }

    /**
     * Добавляет ticket, если он меньше всех элементов в коллекции
     * 
     * @param ticket входной ticket
     */
    public DataContainer addIfMin(Ticket ticket) {
        DataContainer data = new DataContainer();
        if (ticket.compareTo(treeSet.last()) < 0) {

            boolean isAdded = dbManager.addTicket(ticket);
            if(!isAdded) {
                data.add("status", "error");
                data.add("message", "Database error. 0 tickets added");
                return data;
            }

            treeSet.add(ticket);
            data.add("status", "ok");
            data.add("message", "1 ticket added");
            return data;
        }
        data.add("status", "ok");
        data.add("message", "0 ticket added");
        return data;
    }

    /**
     * Удаляет все объекты коллекции больше заданного
     * 
     * @param ticket входной ticket
     */
    public DataContainer removeGreater(Ticket ticket) {
        DataContainer data = new DataContainer();
        int lengthBefore = treeSet.size();


        NavigableSet<Ticket> greaterThan = treeSet.tailSet(ticket, false);
        long cnt = greaterThan.stream()
                .map(t -> t.getOwnerId() == dbManager.getUserId())
                .count();
        if(cnt != greaterThan.size()) {
            data.add("status", "error");
            data.add("message", "Permission denied");
        }
        for(var t : greaterThan) {
            try {
                dbManager.deleteTicket(t.id());
            } catch (Exception e) {
                data.add("status", "error");
                data.add("message", "While deleting items:" + e.getMessage());
                return data;
            }
        }

        treeSet.removeIf(item -> (item.compareTo(ticket) > 0));
        data.add("message", lengthBefore - treeSet.size() + " tickets removed");
        return data;
    }

    /**
     * @return информация о коллекции (кол-во элементов и класс)
     */
    public DataContainer getInfo() {
        DataContainer data = new DataContainer();
        String result = treeSet.getClass().toString() + '\n';
        result += "Number of elements: " + treeSet.size() + '\n';
        data.add("message", "Info message received");
        data.add("return-data", result);
        return data;
    }
}

