package com.labs.server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import com.labs.common.core.Ticket;

/**
 * Контролирует коллекцию
 */
public class CollectionManager {
    /**
     * Коллекция
     */
    private static SortedSet<Ticket> treeSet; // already sync

    /**
     * Конструктор - создание нового объекта
     */
    public CollectionManager() {
         treeSet = Collections.synchronizedSortedSet(new TreeSet<>());
    }

    /**
     * Добавляет объект в колекцию
     * 
     * @param ticket этот объект нужно добавить в колекцию
     */
    public void add(Ticket ticket) {
        treeSet.add(ticket);
    }

    /**
     * Изменяет объект по id
     * 
     * @param id     id объекта в коллекции
     * @param ticket на что заменяем
     */
    public void update(Long id, Ticket ticket) {
        Ticket toRemove = treeSet.stream()
                .filter(t -> t.id().equals(id))
                .findFirst()
                .orElse(null);

        if (toRemove != null) {
            treeSet.remove(toRemove);
        }
        ticket.setId(id);
        treeSet.add(ticket);
    }

    /**
     * @return массив всех объектов коллекции
     */
    public ArrayList<Ticket> getAll() {
        ArrayList<Ticket> tickets = new ArrayList<>(treeSet);
        return tickets;
    }

    /**
     * Удаляет объект из коллекции по его id
     * 
     * @param id id удаляемого объекта
     */
    public void removeById(Long id) {
        treeSet.removeIf(ticket -> (ticket.id() == id));
    }

    /**
     * Очищает коллекцию
     */
    public void clear() {
        treeSet.clear();
    }

    /**
     * Ищет среднюю цену по объектам коллекции
     * 
     * @return средняя цена билетов в коллекции
     */
    public Integer averageOfPrice() {
        if (treeSet.isEmpty())
            return 0;
        AtomicInteger sumPrice = new AtomicInteger(0);
        treeSet.forEach(x -> sumPrice.addAndGet(x.price()));
        Integer avg = sumPrice.get() / treeSet.size();
        return avg;
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
    public Long countGreaterThanRefundable(Boolean refundable) {
        Long count = treeSet.stream()
                .filter(item -> booleanToInt(item.refundable()) > booleanToInt(refundable)) // absurdly, but according
                                                                                            // to TOR
                .count();

        return count;
    }

    /**
     * Возвращает элементы коллекции, значение поля refundable у которых больше
     * заданного
     * 
     * @param refundable входное значение refundable
     * @return элементы коллекции, подходящие под условие
     */
    public ArrayList<Ticket> filterGreaterThanRefundable(Boolean refundable) {
        ArrayList<Ticket> trickets = (ArrayList<Ticket>) treeSet.stream()
                .filter(item -> booleanToInt(item.refundable()) > booleanToInt(refundable)) // absurdly, but according
                                                                                            // to TOR
                .collect(Collectors.toCollection(ArrayList::new));

        return trickets;
    }

    /**
     * Добавляет ticket, если он больше всех элементов в коллекции
     * 
     * @param ticket входной ticket
     */
    public void addIfMax(Ticket ticket) {
        if (ticket.compareTo(treeSet.last()) > 0) {
            treeSet.add(ticket);
        }
    }

    /**
     * Добавляет ticket, если он меньше всех элементов в коллекции
     * 
     * @param ticket входной ticket
     */
    public void addIfMin(Ticket ticket) {
        if (ticket.compareTo(treeSet.last()) < 0) {
            treeSet.add(ticket);
        }
    }

    /**
     * Удаляет все объекты коллекции больше заданного
     * 
     * @param ticket входной ticket
     */
    public void removeGreater(Ticket ticket) {
        treeSet.removeIf(item -> (item.compareTo(ticket) < 0));
    }

    /**
     * @return информация о коллекции (кол-во элементов и класс)
     */
    public String getInfo() {
        String result = treeSet.getClass().toString() + '\n';
        result += "Number of elements: " + treeSet.size() + '\n';
        return result;
    }
}
